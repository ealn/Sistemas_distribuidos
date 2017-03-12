package server;

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
        
        TCPServer.acceptConnetion();
        
        while (!stop)
        {    
            retStr = TCPServer.receive();
            
            if (retStr != null)
            {
                GUIServer.updateMessReceived(retStr);
            }
        }
     }
    
    public void stopThread()
    {
        stop = true;
    }
}
