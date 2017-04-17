package client;

public class Listener extends Thread
{
    private boolean closed = false;
    
    public Listener(String name)
    {
        super(name);
    }
    
    public void run()
    {
        String  retStr = null;
        
        while (!closed)
        {
            retStr = TCPClient.receive();
            ClientConnection.messReceived(retStr);
        }
     }
    
    public void closeListener()
    {
        closed = true;
    }
}
