package server;

import java.util.ArrayList;

public class Connections implements Constants
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
            
            if (client.getClientName().equals(name))
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
        
        //create new client to listening a new connection
        if (createNewClient)
        {
            newClient = createClient();
        }
        
        for (int i = 0; i < clientList.size(); i++)
        {
            client = clientList.get(i);
            
            if (client != newClient)
            {
                str.append(client.getClientName()
                           + " "
                           + client.getAddress()
                           + ":"
                           + client.getPort()
                           + "\n");
            }
        }
        
        GUIServer.updateClientList(str.toString());
    }
    
    public static int messReceived(String str, Client client)
    {
        int ret = SUCCESS;
        
        if (str != null)
        {
            if (str.equals(HELLO))
            {
                GUIServer.updateMessReceived(client.getClientName() 
                                             + " "
                                             + client.getAddress() 
                                             + ":" 
                                             + client.getPort() 
                                             + " connected");
            }
            else if (str.equals(BYE))
            {
                ret = CLOSED;

                GUIServer.updateMessReceived("Closed " 
                                             + client.getClientName() 
                                             + " "
                                             + client.getAddress() 
                                             + ":" 
                                             + client.getPort() );
            }
            else
            {
                GUIServer.updateMessReceived(client.getClientName() 
                                            + " SEND: "
                                            + str);
            }
        }
        else
        {
            ret = CLOSED;
        }
        
        if (ret == CLOSED)
        {
            removeClient(client.getClientName());
            updateClientList(false);
        }
        
        return ret;
    }
}
