package server;

import java.net.Socket;

public class Client extends Thread implements Constants
{
    private Socket  clientSocket = null;
    private String  clientName = null;
    private boolean connected = false;
    private boolean closed = false;
    
    public Client(String name)
    {
        super(name);
        clientName = new String(name);
    }
    
    public void run()
    {
        String  retStr = null;
        
        while (!closed)
        {
            //wait for a connection
            clientSocket = TCPServer.acceptConnetion();
            
            if (clientSocket != null)
            {
                connected = true;
                Connections.updateClientList(true);
        
                while (connected)
                {    
                    retStr = TCPServer.receive(clientSocket);
                    Connections.messReceived(retStr, this);
                }
            }
            else
            {
                closed = true;
            }
        }
    }
    
    public void stopClient()
    {
        connected = false;
        closed = true;
    }
    
    public String getAddress()
    {
        String ret = "";
        
        if (clientSocket != null)
        {
            ret = clientSocket.getInetAddress().getHostAddress();
        }
        
        return ret;
    }
    
    public int getPort()
    {
        int ret = -1;
        
        if (clientSocket != null)
        {
            ret = clientSocket.getPort();
        }
        
        return ret;
    }
    
    public String getClientName()
    {
        return clientName;
    }
}
