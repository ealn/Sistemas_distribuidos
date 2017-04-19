package server;

import java.net.Socket;

public class Client extends Thread implements Constants
{
    private Socket  clientSocket = null;
    private String  clientName = null;
    private TCPServer tcpServer = null;
    private ServerConnections server = null;
    private boolean connected = false;
    private boolean closed = false;
    
    public Client(String name, ServerConnections serverCon, TCPServer tcpser)
    {
        super(name);
        server = serverCon;
        tcpServer = tcpser;
    }
    
    public void run()
    {
        String  retStr = null;
        
        while (!closed)
        {
            //wait for a connection
            clientSocket = tcpServer.acceptConnetion();
            
            if (clientSocket != null)
            {
                connected = true;
                server.updateClientList(true);
        
                while (connected)
                {    
                    retStr = tcpServer.receive(clientSocket);
                    server.messReceived(retStr, this);
                }
            }
            else
            {
                closed = true;
            }
        }
    }
    
    public boolean isConnected()
    {
        return connected;
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
    
    public void setClientName(String name)
    {
        clientName = new String(name);
    }
    
    public String getClientName()
    {
        return clientName;
    }
    
    public void send(String message)
    {
        if (message != null
            && clientSocket != null)
        {
            tcpServer.send(clientSocket, message);
        }
    }
}
