package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class GUIClient implements Constants
{

    private JFrame                             frmTcpClient;
    private static JTextField                  textFieldHost;
    private static JTextField                  textFieldPort;
    private static JTextArea                   textAreaMessSent;
    private static JTextArea                   textAreaMessReceived;
    private static JButton                     btnConnect;
    private static JButton                     btnDisconnect;
    private static JButton                     btnSend;
    private static JButton                     btnSendToAll;
    private static JList                       listClientList;
    private static DefaultListModel <String>   modelClientList;
    private static JTextField                  textFieldUser;
    

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
        frmTcpClient.setBounds(100, 100, 557, 464);
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
        lblMessSent.setBounds(10, 92, 161, 14);
        frmTcpClient.getContentPane().add(lblMessSent);
        
        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setEnabled(false);
        btnDisconnect.setBounds(181, 32, 104, 23);
        frmTcpClient.getContentPane().add(btnDisconnect);
        
        JLabel lblMessageRecieved = new JLabel("Message Recieved");
        lblMessageRecieved.setBounds(10, 249, 213, 14);
        frmTcpClient.getContentPane().add(lblMessageRecieved);
        
        btnSend = new JButton("Send");
        btnSend.setEnabled(false);
        btnSend.setBounds(196, 215, 89, 23);
        frmTcpClient.getContentPane().add(btnSend);
        
        btnSendToAll = new JButton("Send to all");
        btnSendToAll.setEnabled(false);
        btnSendToAll.setBounds(91, 215, 95, 23);
        frmTcpClient.getContentPane().add(btnSendToAll);
        
        JScrollPane scrollPaneMessSent = new JScrollPane();
        scrollPaneMessSent.setBounds(10, 117, 275, 87);
        frmTcpClient.getContentPane().add(scrollPaneMessSent);
        
        textAreaMessSent = new JTextArea();
        scrollPaneMessSent.setViewportView(textAreaMessSent);
        textAreaMessSent.setEnabled(false);
        
        JScrollPane scrollPaneMessReceived = new JScrollPane();
        scrollPaneMessReceived.setBounds(10, 274, 275, 140);
        frmTcpClient.getContentPane().add(scrollPaneMessReceived);
        
        textAreaMessReceived = new JTextArea();
        scrollPaneMessReceived.setViewportView(textAreaMessReceived);
        textAreaMessReceived.setEditable(false);
        textAreaMessReceived.setEnabled(false);
        
        JLabel lblClientList = new JLabel("List of connected clients");
        lblClientList.setBounds(295, 11, 161, 14);
        frmTcpClient.getContentPane().add(lblClientList);
        
        JScrollPane scrollPaneClientList = new JScrollPane();
        scrollPaneClientList.setBounds(295, 34, 236, 118);
        frmTcpClient.getContentPane().add(scrollPaneClientList);
        
        modelClientList = new DefaultListModel<>();
        listClientList = new JList(modelClientList);
        listClientList.setEnabled(false);
        listClientList.setSelectedIndex(0);
        scrollPaneClientList.setViewportView(listClientList);
        
        JLabel lblUser = new JLabel("User");
        lblUser.setBounds(10, 67, 46, 14);
        frmTcpClient.getContentPane().add(lblUser);
        
        textFieldUser = new JTextField();
        textFieldUser.setBounds(38, 61, 133, 20);
        frmTcpClient.getContentPane().add(textFieldUser);
        textFieldUser.setColumns(10);
        
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
        else if (textFieldUser.getText().length() == 0)
        {
            //Show error message
            JOptionPane.showMessageDialog(null,
                                          "Please type the user name",
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
        
        //Send to All
        btnSendToAll.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              if (validateFieldsToSend() == SUCCESS)
              {   
                  sendToAll();
              }
          }
        });
    }
    
    private static void connect()
    {
        String host = textFieldHost.getText();
        String user = textFieldUser.getText();
        int    port = -1;
        int    ret = SUCCESS;
        
        if (textFieldPort.getText().length() != 0)
        {
            port = Integer.parseInt(textFieldPort.getText());
        }
        
        ret = ClientConnection.initConnection(host, port, user);
        
        if (ret == SUCCESS)
        {
            //Communication established
                   
            //Turn on the fields
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);
            btnSendToAll.setEnabled(true);
            listClientList.setEnabled(true);
                    
            textAreaMessSent.setEnabled(true);
            textAreaMessReceived.setEnabled(true);
                    
            textFieldHost.setEditable(false);
            textFieldPort.setEditable(false);
            textFieldUser.setEditable(false);
                    
            textAreaMessSent.setText("");
            textAreaMessReceived.setText("Communication established\n");
        }
        else
        {
            //Show error message       
            if (ret == USER_ALR_USED)
            {
                JOptionPane.showMessageDialog(null,
                                              "User already used",
                                              "Error", 
                                              JOptionPane.ERROR_MESSAGE); 
            }
            else if (ret == FAIL)
            {
                JOptionPane.showMessageDialog(null,
                                              "Communication Error",
                                              "Error", 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void disconnect()
    {
        ClientConnection.stopConnection();
        
        //Turn off the fields
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnSend.setEnabled(false);
        btnSendToAll.setEnabled(false);
        listClientList.setEnabled(false);
        
        textAreaMessSent.setEnabled(false);
        textAreaMessReceived.setEnabled(false);
        
        textFieldHost.setEditable(true);
        textFieldPort.setEditable(true);  
        textFieldUser.setEditable(true);
    }
    
    private static void send()
    {
        ArrayList <String> destination = null;
        
        destination = getSelectedClientList();
        
        if (destination != null)
        {
            ClientConnection.sendMessage(textAreaMessSent.getText(), destination);
        
            //clean text area message sent
            textAreaMessSent.setText("");
        }
        else
        {
            //Show error message
            JOptionPane.showMessageDialog(null,
                                          "Please select a client to send the message",
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            
        }
    }
    
    private static void sendToAll()
    {
        ClientConnection.sendMessageToAll(textAreaMessSent.getText());
        textAreaMessSent.setText("");
    }
    
    public static void updateMessReceived(String str)
    {
        if (str != null)
        {
            textAreaMessReceived.append(str + "\n");
        }
    }
    
    public static void updateClientList(ArrayList <String>list)
    {
        if (list != null)
        {
            modelClientList.removeAllElements();
            
            for (int i = 0; i < list.size(); i++)
            {
                modelClientList.addElement(list.get(i));
            }
            
            listClientList.setSelectedIndex(0);
        }
        else
        {
            modelClientList.removeAllElements();
        }
    }
    
    private static ArrayList<String> getSelectedClientList()
    {
        ArrayList <String> clientList = null;
        int [] seleccions = listClientList.getSelectedIndices();

        if (seleccions.length > 0)
        {
            clientList = new ArrayList<>();
            
            for (int i = 0; i < seleccions.length; i++)
            {
                clientList.add(modelClientList.getElementAt(seleccions[i]));
            }
        }
        
        return clientList;
    }
}
