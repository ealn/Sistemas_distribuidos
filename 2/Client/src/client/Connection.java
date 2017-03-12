package client;

public class Connection extends Thread
{
    private boolean stop = false;
    
    public Connection(String name)
    {
        super(name);
    }
    
    public void run()
    {
        String  retStr = null;
        
        while (!stop)
        {
            retStr = TCPClient.receive();
            
            if (retStr != null)
            {
                GUIClient.updateMessReceived(retStr);
            }
        }
     }
    
    public void stopThread()
    {
        stop = true;
    }
}
