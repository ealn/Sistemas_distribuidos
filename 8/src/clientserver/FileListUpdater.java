package clientserver;

import java.util.ArrayList;

public class FileListUpdater extends Thread implements Constants
{
    private ClientConnection clientConnection= null;
    private ArrayList <FileList>  currentFlist = null;
    private boolean closed = false;
    
    public FileListUpdater(String name, ClientConnection connection)
    {
        super(name);
        clientConnection = connection;
    }
    
    public void run()
    {
        ArrayList <FileList>  newFlist = null;
        ArrayList <FileList>  diffFlist = null;
        
        if (clientConnection != null)
        {
            //get the file list
            currentFlist = clientConnection.createFileList();
            
            while (!closed)
            {
                //run each 1 second
                try 
                {
                    Thread.sleep(1000);
                    newFlist = clientConnection.createFileList();
                    
                    if (newFlist != null)
                    {
                        diffFlist = newFlist.get(0).getDifference(currentFlist, newFlist);
                        
                        //if there are new files
                        if (diffFlist != null)
                        {
                            //send the notification
                            clientConnection.updateLocalFileList();
                            currentFlist = newFlist;
                        }
                    }
                } 
                catch (InterruptedException e) 
                {
                    System.out.println("FileListUpdate.run() " + e.getMessage());
                }
            }
        }
    }
    
    public void closeUpdater()
    {
        closed = true;
    }
}
