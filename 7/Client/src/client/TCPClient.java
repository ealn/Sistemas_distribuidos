package client;

import java.net.*;
import java.io.*;

public class TCPClient implements Constants
{
    private Socket            socket = null;
    private InetAddress       host = null;
    private int               port = 0;
    private DataInputStream   input = null;
    private DataOutputStream  output = null;
    
    public int initConnection(String iHost, int iPort)
    {
        int ret = FAIL;
        
        try 
        {
            if (iPort == -1)
            {
                port = DEFAULT_PORT;
            }
            else
            {
                port = iPort;
            }
            
            host = InetAddress.getByName(iHost);
            socket = new Socket(host, port);
            socket.setKeepAlive( true );
            
            ret = SUCCESS;
        }
        catch (IOException e) 
        {
            System.out.println("initConnection() IO Exception: " + e.getMessage());
        }
        
        return ret;
    }
    
    public int send(String message)
    {
        int ret = SUCCESS;
        
        if (socket != null
            && message != null)
        {
            try 
            {
                //send info
                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(message);
            } 
            catch (IOException e) 
            {
                System.out.println("send() IO Exception: " + e.getMessage());
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    public int sendBytes(byte[] bytes)
    {
        int ret = SUCCESS;
        
        if (socket != null
            && bytes != null)
        {
            try 
            {
                //send info
                output = new DataOutputStream(socket.getOutputStream());
                output.write(bytes);
            } 
            catch (IOException e) 
            {
                System.out.println("send() IO Exception: " + e.getMessage());
            }
        }
        else
        {
            ret = FAIL;
        }
        
        return ret;
    }
    
    public String receive()
    {
        String retStr = null;
        
        if (socket != null)
        {
            try 
            {
                //receive info
                input = new DataInputStream(socket.getInputStream());
                retStr = input.readUTF();
            } 
            catch (IOException e) 
            {
                System.out.println("receive() IO Exception: " + e.getMessage());
            }
        }
        
        return retStr;
    }
    
    public byte[] receiveBytes()
    {
        byte[] retByteArray = null;
        
        if (socket != null)
        {
            try 
            {
                //receive info
                input = new DataInputStream(socket.getInputStream());
                
                if (input != null)
                {
                    retByteArray = new byte[8192];
                    input.read(retByteArray);
                }
            } 
            catch (IOException e) 
            {
                System.out.println("receive() IO Exception: " + e.getMessage());
            }
        }
        
        return retByteArray;
    }
    
    public void closeConnection()
    {
        if (socket != null)
        {
            try 
            {
                socket.close();
            } 
            catch (IOException e) 
            {
                System.out.println("closeConnection() close " + e.getMessage());
            }
        }
    }
}
