package client;

import java.util.ArrayList;
import java.io.File;
import java.lang.StringBuffer;

public class ClientConnection implements Constants
{
    private GUIClient             gui = null;
    private TCPClient             tcpClient = null;
    private Listener              listener = null;
    private String                userName = null;
    private File                  sharedFolder = null;
    private ArrayList <FileList>  flist = null;
    
    public ClientConnection()
    {
        gui = null;
        tcpClient = null;
        listener = null;
        userName = null;
        sharedFolder = null;
        flist = null;
    }
    
    public ClientConnection(GUIClient guiClient)
    {
        gui = guiClient;
        tcpClient = null;
        listener = null;
        userName = null;
        sharedFolder = null;
        flist = null;
    }

    public int initConnection(String host, int port, String user, File shFolder)
    {
        int     ret = SUCCESS;
        String  retStr = null;

        tcpClient = new TCPClient();
        ret = tcpClient.initConnection(host, port);
        
        if (ret == SUCCESS)
        {
            //Send hello
            ret = tcpClient.send(HELLO + USER + user + SEPARATOR);
            
            if (ret == SUCCESS)
            {
                //Wait for an acknowledge
                retStr = tcpClient.receive();
                
                if (retStr != null)
                {
                    if (retStr.equals(ACKNOWLEDGE))
                    {
                        userName = user;
                        sharedFolder = shFolder;
                        ret = SUCCESS;
                    }
                    else if (retStr.equals(USER_ALREADY_USED))
                    {
                        ret = USER_ALR_USED;
                    }
                }
                else
                {
                    ret = FAIL;
                }
            }
        }            

        if (ret == SUCCESS)
        {
            createListener();
            createFileList();
        }
        else
        {
            tcpClient.closeConnection();
        }
        
        return ret;
    }
    
    public void stopConnection()
    {
        //Send bye
        tcpClient.send(BYE);
        
        destroyListener();
     
        tcpClient.closeConnection();
        
        if (gui != null)
        {
            gui.updateClientList(null);
            gui.updateFileList(null);
        }
    }
    
    private void createFileList()
    {
        if (sharedFolder != null)
        {
            File[] files = sharedFolder.listFiles();
            flist = new ArrayList<>();
            FileList file = null;
            
            //Local resources
            //Convert file array to FileList
            for (int i = 0; i < files.length; i++)
            {
                if (!files[i].isDirectory())
                {
                    file = new FileList();
                
                    file.setFileName(files[i].getName());
                    file.setSize(files[i].length());
                    file.setDate(file.formatDate(files[i].lastModified()));
                    file.setOwner(LOCAL);
                    file.setFile(files[i]);
                
                    flist.add(file);
                }
            }
            
            if (gui != null)
            {
                //update GUI file list
                gui.updateFileList(flist);
            }
        }
        
    }
    
    private void createListener()
    {
        listener = new Listener("Listener", this, tcpClient);
        listener.start();
    }
    
    private void destroyListener()
    {
        if (listener != null)
        {
            //stop listening
            listener.closeListener();
        }
    }
    
    public int sendMessageToAll(String str)
    {
        int ret = SUCCESS;
        StringBuffer strBuff = null;
        
        if (str != null)
        {
            strBuff = new StringBuffer(SENDALL);
            
            //Add source
            strBuff.append(SOURCE + userName); 
            
            //Add the message
            strBuff.append(MESSAGE + str + SEPARATOR);
            
            ret = tcpClient.send(strBuff.toString());
        
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    public int sendMessage(String str, ArrayList <String> destination)
    {
        int ret = SUCCESS;
                
        if (str != null
            && destination != null
            && destination.size() > 0)
        {
            StringBuffer strBuff  = new StringBuffer(SEND);
            
            //Add source
            strBuff.append(SOURCE + userName); 
            
            //Add names of users
            for (int i = 0; i < destination.size(); i++)
            {
                strBuff.append(DESTINATION + destination.get(i));
            }
            
            //Add the message
            strBuff.append(MESSAGE + str + SEPARATOR);
            
            //Send message
            ret = tcpClient.send(strBuff.toString());
        
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    public int messReceived(String str)
    {
        int ret = SUCCESS;
        String source = null;
        String message = null;
        Parser parser = new Parser();
        
        if (str != null)
        {
            if (str.equals(ACKNOWLEDGE))
            {
                ret = SUCCESS;
            }
            else if (str.indexOf(UPDATE_USERS) != -1)
            {
                ArrayList <String>list = null;
                
                list = parser.getListOfParameters(str, USER);
                
                if (list != null)
                {
                    if (gui != null)
                    {
                        gui.updateClientList(list);
                    }
                }
                else
                {
                    ret = FAIL;
                }
            }
            else //Message was received
            {
                source = parser.parseString(str, SOURCE);
                message = parser.parseString(str, MESSAGE);
                
                if (source != null
                    && message != null)
                {
                    if (gui != null)
                    {
                        gui.updateMessReceived(source + ": "
                                               + message);
                    }
                }
                else
                {
                    ret = FAIL;
                }
            }
        }
        else
        {
            stopConnection();
        }
        
        return ret;
    }
    
    public File getFileFromFileList(int index)
    {
        File file = null;
        
        if (flist != null
            && index > 0)
        {
            file = flist.get(index).getFile();
        }
        
        return file;
    }
    
    public int downloadFile(File file)
    {
        int ret = SUCCESS;
        
        if (file != null)
        {
            String serStr = null;
            
            SerializeFile serFile = new SerializeFile();
            
            serStr = serFile.serializeAsString(file);
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
}
