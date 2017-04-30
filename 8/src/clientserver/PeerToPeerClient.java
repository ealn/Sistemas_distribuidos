package clientserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerToPeerClient extends Thread implements Constants
{
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ClientConnection connection = null;
    
    public PeerToPeerClient(String name, ClientConnection con)
    {
        super(name);
        connection = con;
    }
    
    public void run()
    {   
        BufferedInputStream bis;
        BufferedOutputStream bos;
        byte[] receivedData;
        int in;
        String filename;
        
        try
        {
            serverSocket = new ServerSocket(DEFAULT_PORT_DOWN);
           
            clientSocket = serverSocket.accept();
           
            receivedData = new byte[BYTES_READ];
            bis = new BufferedInputStream(clientSocket.getInputStream());
            DataInputStream dis=new DataInputStream(clientSocket.getInputStream());
           
            //get file name
            filename = dis.readUTF();
            File file1 = new File(connection.getSharedFolder().getAbsolutePath() + "/" + filename);
            bos = new BufferedOutputStream(new FileOutputStream(file1));
        
            //read
            while ((in = bis.read(receivedData)) != -1)
            {
                bos.write(receivedData,0,in);
            }
            
            bos.close();
            dis.close();
            clientSocket.close();
            serverSocket.close();
            
            connection.showDownloadMessage();
            connection.updateLocalFileList();
       }
       catch (Exception e ) 
       {
         System.err.println(e);
       }    
    } 
}