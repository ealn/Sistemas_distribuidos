package clientserver;

import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread implements Constants
{
    private Socket  clientSocket = null;
    private String  clientName = null;
    private TCPServer tcpServer = null;
    private ServerConnections server = null;
    private ArrayList <FileList>  flist = null;
    private boolean connected = false;
    private boolean closed = false;
    private boolean subscribe_video = false;
    private boolean subscribe_music = false;
    private boolean subscribe_docs = false;
    private boolean subscribe_all = false;
    
    public Client(String name, ServerConnections serverCon, TCPServer tcpser)
    {
        super(name);
        server = serverCon;
        tcpServer = tcpser;
    }
    
    public void run()
    {
        String  retStr = null;
        
        while (!closed)
        {
            //wait for a connection
            clientSocket = tcpServer.acceptConnetion();
            
            if (clientSocket != null)
            {
                connected = true;
                server.updateClientList(true);
        
                while (connected)
                {    
                    retStr = tcpServer.receive(clientSocket);
                    server.messReceived(retStr, this);
                }
            }
            else
            {
                closed = true;
            }
        }
    }
    
    public boolean isConnected()
    {
        return connected;
    }
    
    public void stopClient()
    {
        connected = false;
        closed = true;
    }
    
    public String getAddress()
    {
        String ret = "";
        
        if (clientSocket != null)
        {
            ret = clientSocket.getInetAddress().getHostAddress();
        }
        
        return ret;
    }
    
    public int getPort()
    {
        int ret = -1;
        
        if (clientSocket != null)
        {
            ret = clientSocket.getPort();
        }
        
        return ret;
    }
    
    public void setClientName(String name)
    {
        clientName = new String(name);
    }
    
    public String getClientName()
    {
        return clientName;
    }
    
    public void send(String message)
    {
        if (message != null
            && clientSocket != null)
        {
            tcpServer.send(clientSocket, message);
        }
    }
    
    public void updateFList(int numberOfFiles,
                           ArrayList <String> fnames,
                           ArrayList <String> fsizes,
                           ArrayList <String> fdates,
                           ArrayList <String> fowners)
    {
        flist = new ArrayList<>();
        FileList file = null;
        
        for (int i = 0; i < numberOfFiles; i++)
        {
            file = new FileList();
            file.setFileName(fnames.get(i));
            file.setSize(Integer.parseInt(fsizes.get(i)));
            file.setDate(fdates.get(i));
            file.setOwner(fowners.get(i));
            flist.add(file);
        }
    }
    
    public ArrayList <FileList> getFileList()
    {
        return flist;
    }
    
    public int subscribe(boolean video, boolean music, boolean docs, boolean all)
    {
        int ret = SUCCESS;
        
        subscribe_video = video;
        subscribe_music = music;
        subscribe_docs = docs;  
        subscribe_all = all;
        
        return ret;
    }
    
    public int publish(ArrayList <FileList> diff)
    {
        int ret = SUCCESS;
        
        if (diff != null)
        {
            StringBuffer str = new StringBuffer(PUBLISH);
            String fileName = null;
            String ext = null;
            FileList file = null;
            int      counter = 0;
            
            for (int i = 0; i < diff.size(); i++)
            {
                ext = null;
                file = diff.get(i);
                
                if (file != null
                    && !file.getOwner().equals(clientName))
                {
                    fileName = file.getFileName();
                    ext = fileName.substring(fileName.lastIndexOf("."), 
                                             fileName.length());
                    
                    if (ext != null)
                    {
                        if (subscribe_all)
                        {
                            str.append(MESSAGE + "New file added: " + fileName);
                            counter++;
                        }
                        else
                        {
                            if (subscribe_video
                                && (ext.equals(".avi")
                                    || ext.equals(".mov")
                                    || ext.equals(".mp4")
                                    || ext.equals(".flv")
                                    || ext.equals(".mkv")
                                    || ext.equals(".wmv")
                                    || ext.equals(".mpg")))
                            {
                                str.append(MESSAGE + "New video added: " + fileName);
                                counter++;
                            }
                            else if (subscribe_music
                                     && (ext.equals(".mp3")
                                         || ext.equals(".wav")
                                         || ext.equals(".wma")))
                            {
                                str.append(MESSAGE + "New music added: " + fileName);
                                counter++;
                            }
                            else if (subscribe_docs
                                    && (ext.equals(".doc")
                                        || ext.equals(".docx")
                                        || ext.equals(".xls")
                                        || ext.equals(".xlsx")
                                        || ext.equals(".ppt")
                                        || ext.equals(".pptx")
                                        || ext.equals(".txt")))
                            {
                               str.append(MESSAGE + "New document added: " + fileName);
                               counter++;
                            }
                        }
                    }                            
                }    
            }
            
            str.append(SEPARATOR);
            
            if (counter > 0)
            {
                tcpServer.send(clientSocket, str.toString());
            }
        }
        
        
        return ret;
    }
}
