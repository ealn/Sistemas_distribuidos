package client;

import java.io.*;
import java.net.*;

public class TCPServer implements Constants
{
    private static ServerSocket      socket = null;
    private static int               port = 0;
    private static DataInputStream   input = null;
    private static DataOutputStream  output = null;
    
    public int initServer(int iPort)
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
            
            System.out.println("Socket port: " + socket.getLocalPort());
            
            ret = SUCCESS;
        } 
        catch (IOException e) 
        {
            System.out.println("initServer() IOException " + e.getMessage());
        }
        
        return ret;
    }
    
    public Socket acceptConnetion()
    {
        Socket clientSocket = null;
        
        try 
        {
            if (socket != null)
            {
                clientSocket = socket.accept();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("acceptConnetion() IOException " + e.getMessage());
        }
        
        return clientSocket;
    }
    
    public int send(Socket clientSocket, String message)
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
    
    public int sendBytes(Socket clientSocket, byte[] bytes)
    {
        int ret = SUCCESS;
        
        if (clientSocket != null
            && bytes != null)
        {
            try 
            {
                //send info
                output = new DataOutputStream(clientSocket.getOutputStream());
                output.write(bytes, 0, bytes.length);
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
    
    public String receive(Socket clientSocket)
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
                }
            } 
            catch (IOException e) 
            {
                System.out.println("receive() IO Exception: " + e.getMessage());
            }
        }
        
        return retStr;
    }
    
    public byte[] receiveBytes(Socket clientSocket)
    {
        byte[] retByteArray = null;
        
        if (clientSocket != null)
        { 
            try 
            {
                //receive info
                input = new DataInputStream(clientSocket.getInputStream());

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
    
    public void closeServer()
    {
        if (socket != null)
        {
            try 
            {
                socket.close();
                socket = null;
            } 
            catch (IOException e) 
            {
                System.out.println("closeServer() IO Exception: " + e.getMessage());
            }
        }
    }
}
