package server;

import java.util.ArrayList;

public class ServerConnections implements Constants
{
    private ArrayList<Client> clientList;
    private int               count = 0;
    private GUIServer         gui = null;
    private TCPServer         tcpServer = null;
    
    public int initConnection(GUIServer guiServer, int port)
    {
        int         ret;

        clientList = new ArrayList<Client>();
        count = 0;
        gui = guiServer;
        tcpServer = new TCPServer();
        
        ret = tcpServer.initServer(port);
        createClient();

        return ret;
    }
    
    public void stopConnection()
    {
        Client  client = null;
        
        //stop listening
        if (clientList != null)
        {
            for (int i = 0; i < clientList.size(); i++)
            {
                client = clientList.get(i);
                client.stopClient();
            }
        }
        
        if (tcpServer != null)
        {
            tcpServer.closeServer();
        }
    }
    
    private Client createClient()
    {
        Client  client = null;
        
        client = new Client("Client_" + count, this, tcpServer);

        clientList.add(client);
        count++;
        
        client.start();
        
        return client;
    }
    
    private void removeClient(String name)
    {
        Client  client = null;
        
        for (int i = 0; i < clientList.size(); i++)
        {
            client = clientList.get(i);
            
            if (client.getClientName() == null
                || client.getClientName().equals(name))
            {
                client.stopClient();
                clientList.remove(i);
                break;
            }
        }
    }
    
    public void updateClientList(boolean createNewClient)
    {
        Client       client = null;
        Client       newClient = null;
        StringBuffer str = new StringBuffer();
        StringBuffer sendStr = new StringBuffer();
        
        //create new client to listening a new connection
        if (createNewClient)
        {
            newClient = createClient();
        }
        
        sendStr.append(UPDATE_USERS);
        
        for (int i = 0; i < clientList.size(); i++)
        {
            client = clientList.get(i);
            
            if (client != newClient
                && client.isConnected())
            {
                str.append(client.getClientName()
                           + " "
                           + client.getAddress()
                           + ":"
                           + client.getPort()
                           + "\n");
                
                sendStr.append(USER + client.getClientName());
            }
        }
        
        sendStr.append(SEPARATOR);
        
        if (!createNewClient)
        {
            //send new list to everyone
            sendToAll(sendStr.toString(), null);
            
            //Update server GUI
            if (gui != null)
            {
                gui.updateClientList(str.toString());
            }
        }
    }
    
    private Client getClientByName(String name)
    {
        Client client = null;
        
        if (name != null
            && clientList != null)
        {
            for (int i = 0; i < clientList.size(); i++)
            {
                if (name.equals(clientList.get(i).getClientName()))
                {
                    client = clientList.get(i);
                    break;
                }
            }
        }
        
        return client;
    }
    
    private int updateFileList()
    {     
        int ret = SUCCESS;
        Client       client = null;
        StringBuffer sendStr = new StringBuffer();
        ArrayList <FileList> flistClient = null;
        
        sendStr.append(UPDATE_FLIST);
        
        for (int i = 0; i < clientList.size(); i++)
        {
            client = clientList.get(i);
            
            if (client != null
                && client.isConnected())
            {
                flistClient = client.getFileList();
            
                if (flistClient != null)
                {
                    sendStr.append(F_NUMBER_FILES + flistClient.size());
                    sendStr.append(F_OWNER + client.getClientName());
                    
                    for (int j = 0; j < flistClient.size(); j++)
                    {
                        sendStr.append(F_NAME + flistClient.get(j).getFileName());
                        sendStr.append(F_SIZE + flistClient.get(j).getSize());
                        sendStr.append(F_DATE + flistClient.get(j).getDate());                        
                    }
                }
            }
        }
        
        sendStr.append(SEPARATOR);
        
        if (sendStr != null)
        {
            sendToAll(sendStr.toString(), null);
        }
        
        return ret;
    }
    
    private int getFileList(String str, Client source)
    {
        int ret = SUCCESS;
        
        ArrayList <String> fnames = null;
        ArrayList <String> fsizes = null;
        ArrayList <String> fdates = null;
        ArrayList <String> fowners = null;
        Parser parser = new Parser();
        
        fnames = parser.getListOfParameters(str, F_NAME);
        fsizes = parser.getListOfParameters(str, F_SIZE);
        fdates = parser.getListOfParameters(str, F_DATE);
        fowners = parser.getListOfParameters(str, F_OWNER);
        
        source.updateFList(fnames.size(), fnames, fsizes, fdates, fowners);
        
        return ret;
    }
    
    private int getFileToDownload(String str, Client source)
    {
        int ret = SUCCESS;
        Parser parser = new Parser();
        Client client = null;
        FileList file = null;
        ArrayList <FileList> flistClient = null;
        String fname = null;
        String fsize = null;
        String fdate = null;
        
        fname = parser.parseString(str, F_NAME);
        fsize = parser.parseString(str, F_SIZE);
        fdate = parser.parseString(str, F_DATE);
        
        if (fname != null)
        {
            //search file in the client list
            for (int i = 0; i < clientList.size(); i++)
            {
                client = clientList.get(i);
                
                if (client != null
                    && client.isConnected())
                {
                    flistClient = client.getFileList();
                
                    if (flistClient != null)
                    {
                        //search in the list of files for this client
                        for (int j = 0; j < flistClient.size(); j++)
                        {
                            if (fname.equals(flistClient.get(j).getFileName())
                                && Integer.parseInt(fsize) == flistClient.get(j).getSize()
                                && fdate.equals(flistClient.get(j).getDate()))
                            {
                                file = flistClient.get(j);
                                break;
                            }
                        }
                    }
                }
                
                if (file != null)
                {
                    break;
                }
            }
            
            if (file != null)
            {
                //File found 
                
                //Send the Request to create a Peer to Peer Server
                client.send(CREATE_P2P_SERVER + FILE_DOWN + fname + SEPARATOR);
                
                //Send the Request to create a Peer to Peer Client
                source.send(CREATE_P2P_CLIENT 
                            + IP_P2P_SERVER 
                            + client.getAddress()
                            + SEPARATOR);            
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    private int sendToAll(String message, Client source)
    {
        int ret = SUCCESS;
        Client destination = null;
        
        if (message != null)
        {
            for (int i = 0; i < clientList.size(); i++)
            {
                destination = clientList.get(i);
            
                if (destination != source)
                {
                    destination.send(message);
                }
            }
        }
        
        return ret;
    }
    
    private int sendMessageToAll(String str, Client client)
    {
        int ret = SUCCESS;
        
        if (str != null)
        {
            String source = null;
            String message = null;
            Parser parser = new Parser();
            
            source = parser.parseString(str, SOURCE);
            message = parser.parseString(str, MESSAGE);
            
            if (source != null
                && message != null)
            {
                sendToAll(SEND + SOURCE + source + MESSAGE + message + SEPARATOR, 
                          client);
            }
            else
            {
                ret = FAIL;
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    private int sendMessage(String str)
    {
        int ret = SUCCESS;
        
        if (str != null)
        {
            String source = null;
            String message = null;
            String dest = null;
            Client client = null;
            ArrayList <String> destList = null;
            Parser parser = new Parser();
            
            source = parser.parseString(str, SOURCE);
            message = parser.parseString(str, MESSAGE);
            destList = parser.getListOfParameters(str, DESTINATION);
            
            if (source != null
                && message != null
                && destList != null)
            {
                for (int i = 0; i < destList.size(); i++)
                {
                    dest = destList.get(i);
                    
                    if (dest.equals(SERVER))
                    {
                        //Just update the GUI
                        if (gui != null)
                        {
                            gui.updateMessReceived(source 
                                                   + " SEND: "
                                                   + message);
                        }
                    }
                    else
                    {
                        //Send to the clients
                        client = getClientByName(dest);
                        
                        if (client != null)
                        {
                            client.send(SEND + SOURCE + source + DESTINATION + dest + MESSAGE + message + SEPARATOR);
                        }
                        else
                        {
                            ret = FAIL;
                        }
                    }
                }
            }
            else
            {
                ret = FAIL;
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    public int messReceived(String str, Client client)
    {
        int ret = SUCCESS;
        String substr = null;
        boolean updateList = false;
        boolean updateFList = false;
        Parser parser = new Parser();
        
        if (str != null)
        {
            if (str.indexOf(HELLO) != -1)
            {
                ret = validateNewClient(str, client);
                
                if (ret == SUCCESS)
                {
                    if (gui != null)
                    {
                        gui.updateMessReceived(client.getClientName() 
                                               + " connected");
                    }
                }
                else
                {
                    client.send(USER_ALREADY_USED);
                }
                
                updateList = true;
            }
            else if (str.indexOf(BYE) != -1)
            {
                ret = CLOSED;

                if (gui != null)
                {
                    gui.updateMessReceived(client.getClientName() 
                                             + " disconnected");
                }
                
                updateList = true;
                updateFList = true;
            }
            else if (str.indexOf(SENDALL) != -1)
            {                
                ret = sendMessageToAll(str, client);                
            }
            else if (str.indexOf(SEND) != -1)
            {
                ret = sendMessage(str);
            }
            else if (str.indexOf(SEND_FLIST) != -1)
            {
                ret = getFileList(str, client);
                updateFList = true;
            }
            else if (str.indexOf(DOWNLOAD_FILE) != -1)
            {
                ret = getFileToDownload(str, client);
            }
        }
        else
        {
            ret = CLOSED;
        }
        
        if (ret == SUCCESS)
        {
            client.send(ACKNOWLEDGE);
        }
        else if (ret == CLOSED)
        {
            removeClient(client.getClientName());
        }
        
        if (updateList)
        {
            updateClientList(false);
        }
        if (updateFList)
        {
            updateFileList();
        }
        
        return ret;
    }
    
    private int validateNewClient(String str, Client client)
    {
        int ret = SUCCESS;
        String clientName = null;
        Parser parser = new Parser();
        
        clientName = parser.parseString(str, USER);
        
        if (clientName != null
            && !clientName.equals(SERVER)
            && !isClientNameUsed(clientName))
        {
            client.setClientName(clientName);
        }
        else
        {
            //close the new thread
            ret = CLOSED;
        }
        
        return ret;
    }
    
    private boolean isClientNameUsed(String name)
    {
        boolean ret = false;
        
        if (clientList != null)
        {
            for (int i = 0; i < clientList.size(); i++)
            {
                //if the name is already in the list 
                if (name.equals(clientList.get(i).getClientName()))
                {
                    //return true
                    ret = true;
                }
            }
        }
        
        return ret;
    }
}
