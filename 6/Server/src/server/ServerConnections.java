package server;

import java.util.ArrayList;

public class ServerConnections implements Constants
{
    private static ArrayList<Client> clientList;
    private static int               count = 0;
    
    public static int initConnection(int port)
    {
        int         ret;

        clientList = new ArrayList<Client>();
        count = 0;
        
        ret = TCPServer.initServer(port);
        createClient();

        return ret;
    }
    
    public static void stopConnection()
    {
        Client  client = null;
        
        //stop listening
        for (int i = 0; i < clientList.size(); i++)
        {
            client = clientList.get(i);
            client.stopClient();
        }
        
        TCPServer.closeServer();
    }
    
    private static Client createClient()
    {
        Client  client = null;
        
        client = new Client("Client_" + count);

        clientList.add(client);
        count++;
        
        client.start();
        
        return client;
    }
    
    private static void removeClient(String name)
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
    
    public static void updateClientList(boolean createNewClient)
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
            sentMessageToAll(sendStr.toString(), null);
            
            //Update server GUI
            GUIServer.updateClientList(str.toString());
        }
    }
    
    private static Client getClientByName(String name)
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
    
    private static int sentMessageToAll(String message, Client source)
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
    
    private static int sendMessage(String str)
    {
        int ret = SUCCESS;
        
        if (str != null)
        {
            String source = null;
            String message = null;
            String dest = null;
            Client client = null;
            ArrayList <String> destList = null;
            
            source = Parser.parseString(str, SOURCE);
            message = Parser.parseString(str, MESSAGE);
            destList = Parser.getListOfParameters(str, DESTINATION);
            
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
                        GUIServer.updateMessReceived(source 
                                                     + " SEND: "
                                                     + message);
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
    
    public static int messReceived(String str, Client client)
    {
        int ret = SUCCESS;
        String substr = null;
        boolean updateList = false;
        
        if (str != null)
        {
            if (str.indexOf(HELLO) != -1)
            {
                ret = validateNewClient(str, client);
                
                if (ret == SUCCESS)
                {
                    GUIServer.updateMessReceived(client.getClientName() 
                                                 + " connected");
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

                GUIServer.updateMessReceived(client.getClientName() 
                                             + " disconnected");
                
                updateList = true;
            }
            else if (str.indexOf(SENDALL) != -1)
            {
                substr = Parser.parseString(str, SENDALL);
                
                GUIServer.updateMessReceived("SEND TO ALL: " 
                                             + substr);

                ret = sentMessageToAll(substr, client);
                
            }
            else if (str.indexOf(SEND) != -1)
            {
                ret = sendMessage(str);
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
        
        return ret;
    }
    
    private static int validateNewClient(String str, Client client)
    {
        int ret = SUCCESS;
        String clientName = null;
        
        clientName = Parser.parseString(str, USER);
        
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
    
    private static boolean isClientNameUsed(String name)
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
