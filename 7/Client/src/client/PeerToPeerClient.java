package client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        String fileName = null;
        String size = null;
        long   sizeLong = 0;
        long   sizeReaded = 0;
        long   resultSize = 0;
        File   newFile = null;
        byte[] recBuff = null;
        long   n = 0;
        long   i = 0;
        
        tcpClient.initConnection(host, DEFAULT_PORT_DOWN);
        
        //get file name
        fileName = tcpClient.receive();
        
        if (fileName != null)
        {
            //get file size
            size = tcpClient.receive();       
            sizeLong = Long.parseLong(size);
            
            newFile = new File(connection.getSharedFolder().getAbsolutePath() + "\\" + fileName);
            
            try 
            {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
                
                //get number of chunks
                n = (sizeLong/8192) + 1;
                
                while (i < n)
                {
                    recBuff = tcpClient.receiveBytes();
                    
                    if (recBuff != null)
                    {                   
                        resultSize = sizeLong - sizeReaded;
                        
                        if (resultSize > 8192)
                        {
                            sizeReaded += 8192;
                            out.write(recBuff);
                        }
                        else
                        {
                            byte[] resBuff = new byte[(int)resultSize];
                            
                            System.arraycopy(recBuff, 0, resBuff, 0, resBuff.length);
                            out.write(resBuff);
                            sizeReaded = sizeLong;
                        }                 
                    }
                    
                    i++;
                }
                
                out.flush();
                out.close();
            } 
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        connection.showDownloadMessage();
        connection.updateLocalFileList();
        tcpClient.closeConnection();
    } 
}