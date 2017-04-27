package clientserver;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class PeerToPeerServer extends Thread implements Constants
{
        private TCPServer tcpServer = null;
        private Socket  clientSocket = null;
        private FileList file = null;
        
        public PeerToPeerServer(String name, FileList fileToSend)
        {
            super(name);
            file = fileToSend;
            tcpServer = new TCPServer();
        }
        
        public void run()
        {      
            byte[] byteArray = null;
            long n = 0;
            long i = 0;
            long bytesReaded = 0;
            
            tcpServer.initServer(DEFAULT_PORT_DOWN);
            clientSocket = tcpServer.acceptConnetion();
            
            if (clientSocket != null)
            {
                try 
                {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getFile()));
                    
                    //send the name
                    tcpServer.send(clientSocket, file.getFileName());

                    //send the size
                    tcpServer.send(clientSocket, Long.toString(file.getSize()));
                    
                    //get number of chunks
                    n = (file.getSize()/8192) + 1;
                    
                    //send the file
                    while (i < n)
                    {
                        byteArray = new byte[8192];
                        bis.read(byteArray);
                        tcpServer.sendBytes(clientSocket, byteArray);
                        Thread.sleep(1);
                        i++;
                    }
                    
                    System.out.println("File downloaded");
                    bis.close();
                } 
                catch (FileNotFoundException e) {
                    System.out.println("");
                    e.printStackTrace();
                } 
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            tcpServer.closeServer();
        } 
}
