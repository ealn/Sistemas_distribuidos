package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeFile 
{
    public byte[] serializeAsByteArray(File file)
    {
        byte[] array = null;
        ByteArrayOutputStream byteArray= new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        
        try 
        {
            outputStream = new ObjectOutputStream (byteArray);
            outputStream.writeObject(file);
            outputStream.close();
            array =  byteArray.toByteArray();
            
        } 
        catch (IOException e) 
        {
            System.out.println("serializeAsByteArray() IO Exception: " + e.getMessage());
        }
        
        
        return array;
    }
    
    public String serializeAsString(File file)
    {
        String retStr = null;
        byte []array = null;
        
        array = serializeAsByteArray(file);
        
        if (array != null)
        {
            retStr = new String(array);
        }
        
        return retStr;
    }
    
    public File deserializeByteArray(byte[] array)
    {
        File file = null;
        ByteArrayInputStream byteArray= new ByteArrayInputStream(array);

        try 
        {
            ObjectInputStream input = new ObjectInputStream(byteArray);
            file = (File)input.readObject();
            input.close();            
        } 
        catch (IOException e) 
        {
            System.out.println("deserializeByteArray() IO Exception: " + e.getMessage());
        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println("deserializeByteArray() ClassNotFoundException: " + e.getMessage());
        }
        
        
        return file;
    }
    
    public File deserializeString(String str)
    {
        File file = null;
        
        file = deserializeByteArray(str.getBytes());
        
        return file;
    }
}
