package client;

public class Connection implements Constants
{
    private static Listener listener = null;

    public static int initConnection(String host, int port)
    {
        int     ret = SUCCESS;
        String  retStr = null;

        ret = TCPClient.initConnection(host, port);
        
        if (ret == SUCCESS)
        {
            //Send hello
            ret = TCPClient.send(HELLO);
            
            if (ret == SUCCESS)
            {
                //Wait for an acknowledge
                retStr = TCPClient.receive();
                
                if (retStr != null
                    && retStr.equals(ACKNOWLEDGE))
                {
                    ret = SUCCESS;
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
        
        return ret;
    }
    
    public static void stopConnection()
    {
        //Send bye
        TCPClient.send(BYE);
        
        destroyListener();
     
        TCPClient.closeConnection();
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
            //Adding the @SendAll
            strBuff = new StringBuffer(SENDALL);
            strBuff.append(str);
            
            ret = TCPClient.send(strBuff.toString());
        
            if (ret == FAIL)   
            {
                System.out.println("Send failed");
            }
        }
        
        return ret;
    }
    
    public static int sendMessage(String str)
    {
        int ret = SUCCESS;
        
        if (str != null)
        {
            ret = TCPClient.send(str);
        
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
        
        if (str != null)
        {
            if (str.equals(ACKNOWLEDGE))
            {
                GUIClient.updateMessReceived("Message sent successfully");
            }
            else
            {
                GUIClient.updateMessReceived("Message received: "
                                             + str);
            }
        }
        else
        {
            stopConnection();
        }
        
        return ret;
    }

}
