package clientserver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FileList implements Constants
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
    
    public ArrayList<FileList> getDifference(ArrayList<FileList> list1, ArrayList<FileList> list2)
    {
        ArrayList<FileList> diff = null;
        
        if (list1 != null
            && list2 != null)
        {
            FileList file1 = null;
            FileList file2 = null;
            boolean  found = false;
            
            for (int i = 0; i < list2.size(); i++)
            {
                //get file from list1
                file2 = list2.get(i);
                found = false;
                
                if (file2 != null)
                {
                    //search this file in list2
                    for (int j = 0; j < list1.size(); j++)
                    {
                        //get file from list2
                        file1 = list1.get(j);
                        
                        //if the file is equal
                        if (file1.getFileName().equals(file2.getFileName())
                            && file1.getSize() == file2.getSize())
                        {
                            found = true;
                            break;
                        }
                    }
                    
                    //if the file was not found
                    if (!found)
                    {
                        if (diff == null)
                        {
                            diff = new ArrayList<FileList>();
                        }
                        
                        //add this file to the diff list
                        diff.add(file2);
                    }
                }
            }
        }
        
        return diff;
    }
    
    public int mergeList(ArrayList<FileList> dest, ArrayList<FileList> source)
    {
        int ret = SUCCESS;
        
        if (dest != null
            && source != null)
        {
            ArrayList<FileList> diffList = null;
            FileList file = null;
            
            diffList = getDifference(dest, source);
            
            if (diffList != null)
            {
                for (int i = 0; i < diffList.size(); i++)
                {
                    file = diffList.get(i);
                    
                    if (file != null)
                    {
                        dest.add(file);
                    }
                }
            }
            else
            {
                ret = FAIL;
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
}
