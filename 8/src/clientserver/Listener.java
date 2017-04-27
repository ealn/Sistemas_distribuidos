package clientserver;

public class Listener extends Thread
{
    private boolean closed = false;
    private ClientConnection clientConnection = null;
    private TCPClient tcpClient = null;
    
    public Listener(String name, ClientConnection connection, TCPClient client)
    {
        super(name);
        clientConnection = connection;
        tcpClient = client;
    }
    
    public void run()
    {
        String  retStr = null;
        
        while (!closed)
        {
            retStr = tcpClient.receive();
            clientConnection.messReceived(retStr);
        }
     }
    
    public void closeListener()
    {
        closed = true;
    }
}
