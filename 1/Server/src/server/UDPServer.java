package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer implements Constants
{
    private static DatagramSocket socket = null;
    private static int            port = 0;
    
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
            
            socket = new DatagramSocket(port);
            //socket.setSoTimeout(TIMEOUT);
            
            ret = SUCCESS;
        } 
        catch (SocketException e) 
        {
            System.out.println("Socket Exception " + e.getMessage());
            e.printStackTrace();
        }
        
        return ret;
    }
    
    public static int send(String Message, InetAddress address, int outPort)
    {
        int ret = SUCCESS;
        
        if (socket != null
            && Message != null)
        {
            byte [] mess = Message.getBytes();
            DatagramPacket reply = new DatagramPacket(mess, mess.length, address, outPort);
            
            try 
            {
                //send info
                socket.send(reply);
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
            DatagramPacket request = new DatagramPacket(buff, buff.length);
            
            try 
            {
                //receive information
                socket.receive(request);
                retStr = new String(request.getData());
                
                if (retStr != null)
                {
                    //send an acknowledge
                    send(ACKNOWLEDGE, request.getAddress(), request.getPort());                    
                }
            } 
            catch (IOException e) 
            {
                System.out.println("IO Exception: " + e.getMessage());
            }
        }
        
        return retStr;
    }
    
    public static void closeServer()
    {
        if (socket != null)
        {
            socket.close();
        }
    }
}
