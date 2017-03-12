package client;

import java.net.*;
import java.io.*;

public class UDPClient implements Constants
{
    private static DatagramSocket socket = null;
    private static InetAddress    host = null;
    private static int            port = 0;
    
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
            
            socket = new DatagramSocket();
            host = InetAddress.getByName(iHost);
            //socket.setSoTimeout(TIMEOUT);
            
            ret = SUCCESS;
        } 
        catch (UnknownHostException e) 
        {
            System.out.println("Unknown Host Exception " + e.getMessage());
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            System.out.println("Socket Exception " + e.getMessage());
            e.printStackTrace();
        }
        
        return ret;
    }
    
    public static int send(String Message)
    {
        int ret = SUCCESS;
        
        if (socket != null
            && Message != null)
        {
            byte [] mess = Message.getBytes();
            DatagramPacket request = new DatagramPacket(mess, mess.length, host, port);
            
            try 
            {
                //send info
                socket.send(request);
            } 
            catch (IOException e) 
            {
                System.out.println("IO Exception: " + e.getMessage());
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
            byte []buff = new byte[1024];
            DatagramPacket reply = new DatagramPacket(buff, buff.length);
            
            try 
            {
                //receive information
                socket.receive(reply);
                retStr = new String(reply.getData());
            } 
            catch (IOException e) 
            {
                System.out.println("IO Exception: " + e.getMessage());
            }
        }
        
        return retStr;
    }
    
    public static void closeConnection()
    {
        if (socket != null)
        {
            socket.close();
        }
    }
}
