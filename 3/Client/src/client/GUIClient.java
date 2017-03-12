package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class GUIClient implements Constants
{

    private JFrame             frmTcpClient;
    private static JTextField  textFieldHost;
    private static JTextField  textFieldPort;
    private static JTextArea   textAreaMessSent;
    private static JTextArea   textAreaMessReceived;
    private static JButton     btnConnect;
    private static JButton     btnDisconnect;
    private static JButton     btnSend;

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
                    GUIClient window = new GUIClient();
                    window.frmTcpClient.setVisible(true);
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
    public GUIClient() 
    {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() 
    {
        frmTcpClient = new JFrame();
        frmTcpClient.setTitle("Tcp Client");
        frmTcpClient.setBounds(100, 100, 313, 378);
        frmTcpClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTcpClient.getContentPane().setLayout(null);
        
        btnConnect = new JButton("Connect");
        btnConnect.setBounds(181, 7, 104, 23);
        frmTcpClient.getContentPane().add(btnConnect);
        
        JLabel lblHost = new JLabel("Host");
        lblHost.setBounds(10, 11, 46, 14);
        frmTcpClient.getContentPane().add(lblHost);
        
        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(10, 36, 46, 14);
        frmTcpClient.getContentPane().add(lblPort);
        
        textFieldHost = new JTextField();
        textFieldHost.setBounds(38, 8, 133, 20);
        frmTcpClient.getContentPane().add(textFieldHost);
        textFieldHost.setColumns(10);
        
        textFieldPort = new JTextField();
        textFieldPort.setBounds(38, 33, 133, 20);
        frmTcpClient.getContentPane().add(textFieldPort);
        textFieldPort.setColumns(10);
        
        JLabel lblMessSent = new JLabel("Message Sent");
        lblMessSent.setBounds(10, 69, 161, 14);
        frmTcpClient.getContentPane().add(lblMessSent);
        
        textAreaMessSent = new JTextArea();
        textAreaMessSent.setEnabled(false);
        textAreaMessSent.setBounds(10, 94, 275, 60);
        frmTcpClient.getContentPane().add(textAreaMessSent);
        
        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setEnabled(false);
        btnDisconnect.setBounds(181, 32, 104, 23);
        frmTcpClient.getContentPane().add(btnDisconnect);
        
        JLabel lblMessageRecieved = new JLabel("Message Recieved");
        lblMessageRecieved.setBounds(10, 190, 213, 14);
        frmTcpClient.getContentPane().add(lblMessageRecieved);
        
        textAreaMessReceived = new JTextArea();
        textAreaMessReceived.setEditable(false);
        textAreaMessReceived.setEnabled(false);
        textAreaMessReceived.setBounds(10, 213, 275, 109);
        frmTcpClient.getContentPane().add(textAreaMessReceived);
        
        btnSend = new JButton("Send");
        btnSend.setEnabled(false);
        btnSend.setBounds(196, 163, 89, 23);
        frmTcpClient.getContentPane().add(btnSend);
        
        createActions();
    }
    
    private static int validateConnectionFields()
    {
        int ret = SUCCESS;
        
        if (textFieldHost.getText().length() == 0)
        {
            //Show error message
            JOptionPane.showMessageDialog(null,
                                          "Please type the host name",
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            ret = FAIL;
        }
        
        return ret;
    }
    
    private static int validateFieldsToSend()
    {
        int ret = SUCCESS;
        
        if (textAreaMessSent.getText().length() == 0)
        {
            //Show error message
            JOptionPane.showMessageDialog(null,
                                          "Please type the message to be sent",
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            ret = FAIL;
        }
        
        return ret;
    }
    
    private void createActions()
    {
        //frame closed
        frmTcpClient.addWindowListener(new java.awt.event.WindowAdapter() 
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) 
            {
                 disconnect();
                 System.exit(0);
            }
        });
        
        //Connect
        btnConnect.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              if (validateConnectionFields() == SUCCESS)
              {   
                  connect();
              }
          }
        });
        
        //Disconnect
        btnDisconnect.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              disconnect();
          }
        });
        
        //Send
        btnSend.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              if (validateFieldsToSend() == SUCCESS)
              {   
                  send();
              }
          }
        });
    }
    
    private static void connect()
    {
        String host = textFieldHost.getText();
        int    port = -1;
        int    ret = SUCCESS;
        
        if (textFieldPort.getText().length() != 0)
        {
            port = Integer.parseInt(textFieldPort.getText());
        }
        
        ret = Connection.initConnection(host, port);
        
        if (ret == SUCCESS)
        {
             //Communication established
                   
             //Turn on the fields
             btnConnect.setEnabled(false);
             btnDisconnect.setEnabled(true);
             btnSend.setEnabled(true);
                    
             textAreaMessSent.setEnabled(true);
             textAreaMessReceived.setEnabled(true);
                    
             textFieldHost.setEditable(false);
             textFieldPort.setEditable(false);
                    
             textAreaMessSent.setText("");
             textAreaMessReceived.setText("Communication established\n");
        }
        else
        {
             //Show error message
             JOptionPane.showMessageDialog(null,
                                           "Communication Error",
                                           "Error", 
                                           JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void disconnect()
    {
        Connection.stopConnection();
        
        //Turn off the fields
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnSend.setEnabled(false);
        
        textAreaMessSent.setEnabled(false);
        textAreaMessReceived.setEnabled(false);
        
        textFieldHost.setEditable(true);
        textFieldPort.setEditable(true);
    }
    
    private static void send()
    {
        Connection.sendMessage(textAreaMessSent.getText());
        textAreaMessSent.setText("");
    }
    
    public static void updateMessReceived(String str)
    {
        if (str != null)
        {
            textAreaMessReceived.append(str + "\n");
        }
    }
}
