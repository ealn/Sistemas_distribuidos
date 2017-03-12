package client;

import java.util.ArrayList;
import java.lang.StringBuffer;

public class ClientConnection implements Constants
{
    private static Listener listener = null;
    private static String userName = null; 

    public static int initConnection(String host, int port, String user)
    {
        int     ret = SUCCESS;
        String  retStr = null;

        ret = TCPClient.initConnection(host, port);
        
        if (ret == SUCCESS)
        {
            //Send hello
            ret = TCPClient.send(HELLO + USER + user + SEPARATOR);
            
            if (ret == SUCCESS)
            {
                //Wait for an acknowledge
                retStr = TCPClient.receive();
                
                if (retStr != null)
                {
                    if (retStr.equals(ACKNOWLEDGE))
                    {
                        userName = user;
                        ret = SUCCESS;
                    }
                    else if (retStr.equals(USER_ALREADY_USED))
                    {
                        ret = USER_ALR_USED;
                    }
                }
                else
                {
                    ret = FAIL;
                }
            }
        }            

        if (ret == SUCCESS)
        {
            createListener();
        }
        else
        {
            TCPClient.closeConnection();
        }
        
        return ret;
    }
    
    public static void stopConnection()
    {
        //Send bye
        TCPClient.send(BYE);
        
        destroyListener();
     
        TCPClient.closeConnection();
        
        GUIClient.updateClientList(null);
    }
    
    private static void createListener()
    {
        listener = new Listener("Listener");
        listener.start();
    }
    
    private static void destroyListener()
    {
        if (listener != null)
        {
            //stop listening
            listener.closeListener();
        }
    }
    
    public static int sendMessageToAll(String str)
    {
        int ret = SUCCESS;
        StringBuffer strBuff = null;
        
        if (str != null)
        {
            strBuff = new StringBuffer(SENDALL);
            
            //Add source
            strBuff.append(SOURCE + userName); 
            
            //Add the message
            strBuff.append(MESSAGE + str + SEPARATOR);
            
            ret = TCPClient.send(strBuff.toString());
        
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    public static int sendMessage(String str, ArrayList <String> destination)
    {
        int ret = SUCCESS;
                
        if (str != null
            && destination != null
            && destination.size() > 0)
        {
            StringBuffer strBuff  = new StringBuffer(SEND);
            
            //Add source
            strBuff.append(SOURCE + userName); 
            
            //Add names of users
            for (int i = 0; i < destination.size(); i++)
            {
                strBuff.append(DESTINATION + destination.get(i));
            }
            
            //Add the message
            strBuff.append(MESSAGE + str + SEPARATOR);
            
            //Send message
            ret = TCPClient.send(strBuff.toString());
        
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    public static int messReceived(String str)
    {
        int ret = SUCCESS;
        String source = null;
        String message = null;
        
        if (str != null)
        {
            if (str.equals(ACKNOWLEDGE))
            {
                ret = SUCCESS;
            }
            else if (str.indexOf(UPDATE_USERS) != -1)
            {
                ArrayList <String>list = null;
                
                list = Parser.getListOfParameters(str, USER);
                
                if (list != null)
                {
                    GUIClient.updateClientList(list);
                }
                else
                {
                    ret = FAIL;
                }
            }
            else //Message was received
            {
                source = Parser.parseString(str, SOURCE);
                message = Parser.parseString(str, MESSAGE);
                
                if (source != null
                    && message != null)
                {
                    GUIClient.updateMessReceived(source + ": "
                                                 + message);
                }
                else
                {
                    ret = FAIL;
                }
            }
        }
        else
        {
            stopConnection();
        }
        
        return ret;
    }
}
