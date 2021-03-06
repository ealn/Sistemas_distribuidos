package server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

public class GUIServer implements Constants
{

    private JFrame            frmUdpServer;
    private static JTextField textFieldPort;
    private static JButton    btnInitServer;
    private static JButton    btnCloseServer;
    private static JTextArea  textAreaMessReceived;
    private static Connection    threadReceive = null;

    /**
     * Launch the application.
     */
    public static void main(String[] args) 
    {
        EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    GUIServer window = new GUIServer();
                    window.frmUdpServer.setVisible(true);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GUIServer() 
    {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() 
    {
        frmUdpServer = new JFrame();
        frmUdpServer.setTitle("UDP Server");
        frmUdpServer.setBounds(100, 100, 307, 397);
        frmUdpServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmUdpServer.getContentPane().setLayout(null);
        
        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(10, 11, 44, 14);
        frmUdpServer.getContentPane().add(lblPort);
        
        textFieldPort = new JTextField();
        textFieldPort.setBounds(48, 8, 108, 20);
        frmUdpServer.getContentPane().add(textFieldPort);
        textFieldPort.setColumns(10);
        
        btnInitServer = new JButton("Init Server");
        btnInitServer.setBounds(166, 7, 116, 23);
        frmUdpServer.getContentPane().add(btnInitServer);
        
        btnCloseServer = new JButton("Close Server");
        btnCloseServer.setEnabled(false);
        btnCloseServer.setBounds(166, 41, 116, 23);
        frmUdpServer.getContentPane().add(btnCloseServer);
        
        JLabel lblMessageReceived = new JLabel("Message Received");
        lblMessageReceived.setBounds(10, 69, 146, 14);
        frmUdpServer.getContentPane().add(lblMessageReceived);
        
        textAreaMessReceived = new JTextArea();
        textAreaMessReceived.setEnabled(false);
        textAreaMessReceived.setEditable(false);
        textAreaMessReceived.setBounds(10, 94, 272, 253);
        frmUdpServer.getContentPane().add(textAreaMessReceived);
        
        createActions();
    }
    
    private static void createActions()
    {
        //Init
        btnInitServer.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              init();
          }
        });
        
        //Close
        btnCloseServer.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              close();
          }
        });
    }
    
    private static int init()
    {
        int     port = -1;
        int     ret = SUCCESS;
        
        threadReceive = new Connection("TReceived");
        
        if (textFieldPort.getText().length() != 0)
        {
            port = Integer.parseInt(textFieldPort.getText());
        }
        
        ret = UDPServer.initServer(port);
        
        if (ret == SUCCESS)
        {
            //Turn off components
            btnInitServer.setEnabled(false);
            btnCloseServer.setEnabled(true);
            textFieldPort.setEnabled(false);
            textAreaMessReceived.setEnabled(true);
            
            threadReceive.start();
        }

        return ret;
    }
    
    private static void close()
    {
        if (threadReceive != null)
        {
            threadReceive.stopThread();
        }
        
        UDPServer.closeServer();
        
        //Turn on components
        btnInitServer.setEnabled(true);
        btnCloseServer.setEnabled(false);
        textFieldPort.setEnabled(true);
        textAreaMessReceived.setEnabled(false);
    }
    
    public static void updateMessReceived(String str)
    {
        if (str != null)
        {
            textAreaMessReceived.append(str + "\n");
        }
    }
}
