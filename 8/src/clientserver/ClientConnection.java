package clientserver;

import java.util.ArrayList;

import java.io.File;
import java.lang.StringBuffer;

public class ClientConnection implements Constants
{
    private GUI                   gui = null;
    private TCPClient             tcpClient = null;
    private Listener              listener = null;
    private String                userName = null;
    private File                  sharedFolder = null;
    private ArrayList <FileList>  flist = null;
    private ServerConnections     server = null;
    private FileListUpdater       updater = null;
    
    public ClientConnection()
    {
        gui = null;
        tcpClient = null;
        listener = null;
        userName = null;
        sharedFolder = null;
        flist = null;
    }
    
    public ClientConnection(GUI guiClient)
    {
        gui = guiClient;
        tcpClient = null;
        listener = null;
        userName = null;
        sharedFolder = null;
        flist = null;
    }
    
    public File getSharedFolder()
    {
        return sharedFolder;
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
            updateLocalFileList();
            createFileListUpdater();
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
        if (tcpClient != null)
        {
            tcpClient.send(BYE);
        }
        
        destroyListener();
        destroyFileListUpdater();
     
        if (tcpClient != null)
        {
            tcpClient.closeConnection();
        }
            
        if (gui != null)
        {
            gui.updateClientList(null);
            gui.updateFileList(null);
        }
        
        stopServer();
    }
    
    public int initServer()
    {
        int ret = SUCCESS;
        
        server = new ServerConnections();
        
        if (server != null)
        {
            ret = server.initConnection(gui, DEFAULT_PORT);
            
            if (ret == FAIL)
            {
                server = null;
            }
        }
        
        return ret;
    }
    
    public void stopServer()
    {
        if (server != null)
        {
            server.stopConnection();
        }
    }
    
    public void showDownloadMessage()
    {
        if (gui != null)
        {
            gui.showDownloadMessageDialog();
        }
    }
    
    public void updateLocalFileList()
    {
        flist = createFileList();
        
        if (flist != null)
        {
            sendFileList();
        }
    }
    
    public ArrayList <FileList> createFileList()
    {
        ArrayList <FileList> fileList = null;
        
        if (sharedFolder != null)
        {
            File[] files = sharedFolder.listFiles();
            fileList = new ArrayList<>();
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
                
                    fileList.add(file);
                }
            }
        }
        
        return fileList;
    }
    
    private int sendFileList()
    {
        int ret = SUCCESS;
        
        if (flist != null)
        {
            StringBuffer strBuff = null;
            FileList     file = null;
        
            strBuff = new StringBuffer(SEND_FLIST);
        
            //Add source
            strBuff.append(SOURCE + userName); 
        
            //Add the list
            for (int i = 0; i < flist.size(); i++)
            {
                file = flist.get(i);
            
                if (file.getOwner().equals(LOCAL))
                {
                    strBuff.append(F_NAME + file.getFileName() + SEPARATOR);
                    strBuff.append(F_SIZE + file.getSize() + SEPARATOR);
                    strBuff.append(F_DATE + file.getDate() + SEPARATOR);
                    strBuff.append(F_OWNER + userName + SEPARATOR);
                }
            }
        
            ret = tcpClient.send(strBuff.toString());
    
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    private ArrayList<FileList> getNetworkResources(String str)
    {
        ArrayList<FileList> netList = null;
        
        if (str != null)
        {
            FileList file = null;
            int numFiles = 0;
            int j = 0;
            int i = 0;
            ArrayList <String> numberOfFiles = null;
            ArrayList <String> fnames = null;
            ArrayList <String> fsizes = null;
            ArrayList <String> fdates = null;
            ArrayList <String> fowners = null;
            Parser parser = new Parser();
        
            numberOfFiles = parser.getListOfParameters(str, F_NUMBER_FILES);
            fowners = parser.getListOfParameters(str, F_OWNER);
            fnames = parser.getListOfParameters(str, F_NAME);
            fsizes = parser.getListOfParameters(str, F_SIZE);
            fdates = parser.getListOfParameters(str, F_DATE);
            
            if (numberOfFiles != null)
            {
                //search by owners
                for (i = 0; i < fowners.size(); i++)
                {    
                     numFiles += Integer.parseInt(numberOfFiles.get(i));
                    
                     if (!fowners.get(i).equals(userName))
                     {  
                         //Add files of this owner
                         while (j < numFiles)
                         {
                             file = new FileList();
                           
                             file.setFileName(fnames.get(j));
                             file.setSize(Integer.parseInt(fsizes.get(j)));
                             file.setDate(fdates.get(j));
                             file.setOwner(NET);
                            
                             if (netList == null)
                             {
                                 netList = new ArrayList<FileList>();
                             }
                             
                             netList.add(file);
                             j++;
                         }
                     }
                     else
                     {
                         j = numFiles;
                     }
                }
            }
        }
        
        return netList;
    }
    
    private int updateFileList(String str)
    {
        int ret = SUCCESS;
        ArrayList<FileList> netList = null;
        
        flist = createFileList();
        netList = getNetworkResources(str);
        
        if (flist != null)
        {
            flist.get(0).mergeList(flist, netList);
            
            if (gui != null)
            {
                 //update GUI file list
                 gui.updateFileList(flist);
            }
        }

        return ret;
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
    
    private void createFileListUpdater()
    {
        updater = new FileListUpdater("Updater", this);
        updater.start();
    }
    
    private void destroyFileListUpdater()
    {
        if (updater != null)
        {
            //stop updater
            updater.closeUpdater();
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
            else if (str.indexOf(UPDATE_FLIST) != -1)
            {
                ret = updateFileList(str);
            }
            else if (str.indexOf(CREATE_P2P_SERVER) != -1)
            {
                ret = createP2PServer(str);
            }
            else if (str.indexOf(CREATE_P2P_CLIENT) != -1)
            {
                ret = createP2PClient(str);
            }
            else if (str.indexOf(PUBLISH) != -1)
            {
                ret = publish(str);
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
    
    public FileList getFileListFromIndex(int index)
    {
        FileList file = null;
        
        if (flist != null
            && index > 0)
        {
            file = flist.get(index);
        }
        
        return file;
    }
    
    public FileList getFileListFromName(String name)
    {
        FileList file = null;
        
        if (flist != null
            && name != null)
        {
            for (int i = 0; i < flist.size(); i++)
            {
                if (flist.get(i).getOwner().equals(LOCAL)
                    && flist.get(i).getFileName().equals(name))
                {
                    file = flist.get(i);
                }
            }
        }
        
        return file;
    }
    
    public int downloadFile(FileList file)
    {
        int ret = SUCCESS;
        
        if (file != null)
        {
            if (file.getOwner().equals(NET))
            {
                StringBuffer strBuff = new StringBuffer();
                
                //Send request                
                strBuff.append(DOWNLOAD_FILE); 
                
                strBuff.append(F_NAME + file.getFileName() + SEPARATOR);
                strBuff.append(F_SIZE + file.getSize() + SEPARATOR);
                strBuff.append(F_DATE + file.getDate() + SEPARATOR);
                strBuff.append(F_OWNER + NET + SEPARATOR);
                
                ret = tcpClient.send(strBuff.toString());
                
                if (ret == FAIL)   
                {
                    System.out.println("Send failed");
                }
            }
            else
            {
                ret = FILE_ALR_DOWN;
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    private int createP2PServer(String str)
    {
        int ret = SUCCESS;
        PeerToPeerServer p2pServer = null;
        String fname = null;
        FileList fileToSend = null;
        String host = null;
        Parser parser = new Parser();
        
        fname = parser.parseString(str, FILE_DOWN);
        fileToSend = getFileListFromName(fname);
        host = parser.parseString(str, IP_P2P_SERVER);
        
        if (fileToSend != null
            && host != null)
        {
            if (host.equals("127.0.0.1")) //localhost
            {
                //use the address of the socket for this client
                p2pServer = new PeerToPeerServer("P2PServer", 
                                                 fileToSend, 
                                                 tcpClient.getSocket().getInetAddress().getHostAddress());
            }
            else
            {
                p2pServer = new PeerToPeerServer("P2PServer", fileToSend, host);
            }
  
            p2pServer.start();
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    private int createP2PClient(String str)
    {
        int ret = SUCCESS;
        PeerToPeerClient p2pClient = null;

        //use the address of the socket for this client
        p2pClient = new PeerToPeerClient("P2PClient", this);
        p2pClient.start();

        return ret;
    }
    
    public int subscribe(boolean video, boolean music, boolean docs, boolean all)
    {
        int ret = SUCCESS;
        StringBuffer strBuff = null;
        
        if (video || music || docs || all)
        {
            strBuff = new StringBuffer(SUBSCRIBE);
            
            //Add subscribe modules
            if (video)
            {
                strBuff.append(SUBSCRIBE_VIDEO + 1); 
            }
            if (music)
            {
                strBuff.append(SUBSCRIBE_MUSIC + 1); 
            }
            if (docs)
            {
                strBuff.append(SUBSCRIBE_DOCS + 1); 
            }
            if (all)
            {
                strBuff.append(SUBSCRIBE_ALL + 1); 
            }
            
            //Add the message
            strBuff.append(SEPARATOR);
            
            ret = tcpClient.send(strBuff.toString());
        
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    public int publish(String str)
    {
        int ret = SUCCESS;
        
        if (str != null)
        {
            Parser parser = new Parser();
            ArrayList <String>list = null;
            
            list = parser.getListOfParameters(str, MESSAGE);
        
            if (list != null)
            {
                if (gui != null)
                {
                    for (int i = 0; i < list.size(); i++)
                    {
                        gui.updateMessReceived(list.get(i));
                    }
                }
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
}
