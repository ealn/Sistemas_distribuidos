package client;

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
            String  fileSer = null;
            SerializeFile serialize = new SerializeFile();
                       
            tcpServer.initServer(DEFAULT_PORT_DOWN);
            clientSocket = tcpServer.acceptConnetion();
            fileSer = serialize.serializeAsString(file.getFile());
            
            if (clientSocket != null)
            {
                tcpServer.send(clientSocket, fileSer);
            }
            
            tcpServer.closeServer();
        } 
}
