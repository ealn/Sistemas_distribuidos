package client;

import java.io.File;

public class PeerToPeerClient extends Thread implements Constants
{
    private String host = null;
    private TCPClient tcpClient = null;
    private ClientConnection connection = null;
    
    public PeerToPeerClient(String name, String hostName, ClientConnection con)
    {
        super(name);
        host = hostName;
        tcpClient = new TCPClient();
        connection = con;
    }
    
    public void run()
    {
        String FileStr = null;
        File   file = null;
        File   newFile = null;
        SerializeFile serialize = new SerializeFile();
        
        tcpClient.initConnection(host, DEFAULT_PORT_DOWN);
        FileStr = tcpClient.receive();
        
        if (FileStr != null)
        {
            file = serialize.deserializeString(FileStr);
            
            if (file != null)
            {
                newFile = new File(sharedFolder.getAbsolutePath() + file.getName());
            }
        }
        
        tcpClient.closeConnection();
    } 
}