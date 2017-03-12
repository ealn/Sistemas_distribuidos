package server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUIServer implements Constants
{

    private JFrame             frmTcpServer;
    private static JTextField  textFieldPort;
    private static JButton     btnInitServer;
    private static JButton     btnCloseServer;
    private static JTextArea   textAreaClientList;
    private static JTextArea   textAreaMessReceived;
    private JScrollPane scrollPaneClientList;
    private JScrollPane scrollPaneMessReceived;

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
                    window.frmTcpServer.setVisible(true);
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
        frmTcpServer = new JFrame();
        frmTcpServer.setTitle("TCP Server");
        frmTcpServer.setBounds(100, 100, 308, 589);
        frmTcpServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTcpServer.getContentPane().setLayout(null);
        
        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(10, 11, 44, 14);
        frmTcpServer.getContentPane().add(lblPort);
        
        textFieldPort = new JTextField();
        textFieldPort.setBounds(48, 8, 108, 20);
        frmTcpServer.getContentPane().add(textFieldPort);
        textFieldPort.setColumns(10);
        
        btnInitServer = new JButton("Init Server");
        btnInitServer.setBounds(166, 7, 116, 23);
        frmTcpServer.getContentPane().add(btnInitServer);
        
        JLabel lblClientList = new JLabel("Client List");
        lblClientList.setBounds(10, 50, 94, 14);
        frmTcpServer.getContentPane().add(lblClientList);
        
        btnCloseServer = new JButton("Close Server");
        btnCloseServer.setEnabled(false);
        btnCloseServer.setBounds(166, 41, 116, 23);
        frmTcpServer.getContentPane().add(btnCloseServer);
        
        JLabel lblMessageReceived = new JLabel("Message Received");
        lblMessageReceived.setBounds(10, 261, 146, 14);
        frmTcpServer.getContentPane().add(lblMessageReceived);
        
        scrollPaneClientList = new JScrollPane();
        scrollPaneClientList.setBounds(10, 75, 272, 175);
        frmTcpServer.getContentPane().add(scrollPaneClientList);
        
        textAreaClientList = new JTextArea();
        scrollPaneClientList.setViewportView(textAreaClientList);
        textAreaClientList.setEnabled(false);
        textAreaClientList.setEditable(false);
        
        scrollPaneMessReceived = new JScrollPane();
        scrollPaneMessReceived.setBounds(10, 286, 272, 253);
        frmTcpServer.getContentPane().add(scrollPaneMessReceived);
        
        textAreaMessReceived = new JTextArea();
        scrollPaneMessReceived.setViewportView(textAreaMessReceived);
        textAreaMessReceived.setEnabled(false);
        textAreaMessReceived.setEditable(false);
        
        createActions();
    }
    
    private void createActions()
    {
        frmTcpServer.addWindowListener(new java.awt.event.WindowAdapter() 
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) 
            {
                 close();
                 System.exit(0);
            }
        });
        
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
    
    private int init()
    {
        int     port = -1;
        int     ret = SUCCESS;
        
        if (textFieldPort.getText().length() != 0)
        {
            port = Integer.parseInt(textFieldPort.getText());
        }
        
        ret = ServerConnections.initConnection(port);
        
        if (ret == SUCCESS)
        {
            //Turn off components
            btnInitServer.setEnabled(false);
            btnCloseServer.setEnabled(true);
            textFieldPort.setEnabled(false);
            textAreaClientList.setEnabled(true);
            textAreaMessReceived.setEnabled(true);
            
            textAreaClientList.setText("");
            textAreaMessReceived.setText("Server connected\n");
        }

        return ret;
    }
    
    private void close()
    {
        ServerConnections.stopConnection();       
        
        //Turn on components
        btnInitServer.setEnabled(true);
        btnCloseServer.setEnabled(false);
        textFieldPort.setEnabled(true);
        textAreaClientList.setEnabled(false);
        textAreaMessReceived.setEnabled(false);
    }
    
    public static void updateMessReceived(String str)
    {
        if (str != null)
        {
            textAreaMessReceived.append(str + "\n");
        }
    }
    
    public static void updateClientList(String str)
    {
        if (str != null)
        {
            textAreaClientList.setText(str);
        }
    }
}
