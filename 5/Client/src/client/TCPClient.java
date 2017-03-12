package client;

import java.net.*;
import java.io.*;

public class TCPClient implements Constants
{
    private static Socket            socket = null;
    private static InetAddress       host = null;
    private static int               port = 0;
    private static DataInputStream   input = null;
    private static DataOutputStream  output = null;
    
    public static int initConnection(String iHost, int iPort)
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
            
            ret = SUCCESS;
        }
        catch (IOException e) 
        {
            System.out.println("initConnection() IO Exception: " + e.getMessage());
        }
        
        return ret;
    }
    
    public static int send(String message)
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
    
    public static String receive()
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
    
    public static void closeConnection()
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
