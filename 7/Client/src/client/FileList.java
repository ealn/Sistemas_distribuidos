package client;

import java.io.File;
import java.text.SimpleDateFormat;

public class FileList 
{
    private String fileName = null;
    private long   size = 0;
    private String date = null;
    private String owner = null;
    private File   file = null;
            
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fName)
    {
        fileName = fName;
    }
    
    public String getOwner()
    {
        return owner;
    }
    
    public void setOwner(String own)
    {
        owner = own;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String dte)
    {
        date = dte;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long sze)
    {
        size = sze;
    }
    
    public void setFile(File f)
    {
        file = f;
    }
    
    public File getFile()
    {
        return file;
    }
    
    public String formatDate(long date)
    {
        String ret = null;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        ret = formatter.format(date);
        
        return ret;
    }
}
