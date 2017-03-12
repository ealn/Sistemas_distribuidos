package server;

import java.io.*;
import java.net.*;

public class TCPServer implements Constants
{
    private static ServerSocket      socket = null;
    private static int               port = 0;
    private static DataInputStream   input = null;
    private static DataOutputStream  output = null;
    private static Socket            clientSocket = null;
    
    public static int initServer(int iPort)
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
            
            socket = new ServerSocket(port);
            
            ret = SUCCESS;
        } 
        catch (IOException e) 
        {
            System.out.println("initServer() IOException " + e.getMessage());
        }
        
        return ret;
    }
    
    public static void acceptConnetion()
    {
        try 
        {
            clientSocket = socket.accept();
        } 
        catch (IOException e) 
        {
            System.out.println("acceptConnetion() IOException " + e.getMessage());
        }
    }
    
    public static int send(String message)
    {
        int ret = SUCCESS;
        
        if (clientSocket != null
            && message != null)
        {
            try 
            {
                //send info
                output = new DataOutputStream(clientSocket.getOutputStream());
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
        
        if (clientSocket != null)
        { 
            try 
            {
                //receive info
                input = new DataInputStream(clientSocket.getInputStream());

                if (input != null)
                {
                    retStr = input.readUTF();
                    
                    if (retStr != null)
                    {
                        //send a reply
                        send(ACKNOWLEDGE);
                    }
                }
            } 
            catch (IOException e) 
            {
                System.out.println("receive() IO Exception: " + e.getMessage());
            }
        }
        
        return retStr;
    }
    
    public static void closeServer()
    {
        if (socket != null)
        {
            try 
            {
                if (clientSocket != null)
                {
                    clientSocket.close();
                }
                
                socket.close();
            } 
            catch (IOException e) 
            {
                System.out.println("closeServer() IO Exception: " + e.getMessage());
            }
        }
    }
}
