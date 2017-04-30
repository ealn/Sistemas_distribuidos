package clientserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class PeerToPeerServer extends Thread implements Constants
{
        private Socket   clientSocket = null;
        private FileList file = null;
        private String   host = null;
        
        public PeerToPeerServer(String name, FileList fileToSend, String hostname)
        {
            super(name);
            file = fileToSend;
            host = hostname;
        }
        
        public void run()
        {      
            BufferedInputStream bis;
            BufferedOutputStream bos;
            int in;
            byte[] byteArray;
            
            try
            {
                File localFile = file.getFile();
                clientSocket = new Socket(host, DEFAULT_PORT_DOWN);
                
                bis = new BufferedInputStream(new FileInputStream(localFile));
                bos = new BufferedOutputStream(clientSocket.getOutputStream());
            
                DataOutputStream dos=new DataOutputStream(clientSocket.getOutputStream());
                dos.writeUTF(localFile.getName());
            
                byteArray = new byte[BYTES_SENT];
            
                while ((in = bis.read(byteArray)) != -1)
                {
                    bos.write(byteArray,0,in);
                }
            
                bis.close();
                bos.close();
                clientSocket.close();            
           }
           catch ( Exception e ) 
           {
               System.err.println(e);
           }
        } 
}
