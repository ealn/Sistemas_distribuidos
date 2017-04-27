package clientserver;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTable;

public class GUI implements Constants
{

    private JFrame                      frmTcpClient;
    private JTextField                  textFieldHost;
    private JTextField                  textFieldPort;
    private JTextField                  textFieldUser;
    private JTextField                  textFieldSharedFolder;
    private JTextArea                   textAreaMessSent;
    private JTextArea                   textAreaMessReceived;
    private JButton                     btnConnect;
    private JButton                     btnDisconnect;
    private JButton                     btnSend;
    private JButton                     btnSendToAll;
    private JButton                     btnBrowse;
    private JButton                     btnDownloadFile;
    private JButton                     btnInitServer;
    private JButton                     btnCloseServer;
    private JList                       listClientList;
    private DefaultListModel <String>   modelClientList;
    private JTable                      tableFileList;
    private DefaultTableModel           tableModelFileList;
    private ClientConnection            clientConnection;
    private boolean                     serverOnline = false;
        
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
                    GUI window = new GUI();
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
    public GUI() 
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
        frmTcpClient.setBounds(100, 100, 690, 586);
        frmTcpClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTcpClient.getContentPane().setLayout(null);
        
        btnConnect = new JButton("Connect");
        btnConnect.setBounds(181, 7, 110, 23);
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
        lblMessSent.setBounds(10, 395, 161, 14);
        frmTcpClient.getContentPane().add(lblMessSent);
        
        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setEnabled(false);
        btnDisconnect.setBounds(181, 32, 110, 23);
        frmTcpClient.getContentPane().add(btnDisconnect);
        
        JLabel lblMessageRecieved = new JLabel("Message Recieved");
        lblMessageRecieved.setBounds(10, 132, 213, 14);
        frmTcpClient.getContentPane().add(lblMessageRecieved);
        
        btnSend = new JButton("Send");
        btnSend.setEnabled(false);
        btnSend.setBounds(196, 514, 89, 23);
        frmTcpClient.getContentPane().add(btnSend);
        
        btnSendToAll = new JButton("Send to all");
        btnSendToAll.setEnabled(false);
        btnSendToAll.setBounds(94, 514, 95, 23);
        frmTcpClient.getContentPane().add(btnSendToAll);
        
        JScrollPane scrollPaneMessSent = new JScrollPane();
        scrollPaneMessSent.setBounds(10, 420, 275, 87);
        frmTcpClient.getContentPane().add(scrollPaneMessSent);
        
        textAreaMessSent = new JTextArea();
        scrollPaneMessSent.setViewportView(textAreaMessSent);
        textAreaMessSent.setEnabled(false);
        
        JScrollPane scrollPaneMessReceived = new JScrollPane();
        scrollPaneMessReceived.setBounds(10, 157, 275, 227);
        frmTcpClient.getContentPane().add(scrollPaneMessReceived);
        
        textAreaMessReceived = new JTextArea();
        scrollPaneMessReceived.setViewportView(textAreaMessReceived);
        textAreaMessReceived.setEditable(false);
        textAreaMessReceived.setEnabled(false);
        
        JLabel lblClientList = new JLabel("List of connected clients");
        lblClientList.setBounds(304, 11, 161, 14);
        frmTcpClient.getContentPane().add(lblClientList);
        
        JScrollPane scrollPaneClientList = new JScrollPane();
        scrollPaneClientList.setBounds(295, 34, 261, 118);
        frmTcpClient.getContentPane().add(scrollPaneClientList);
        
        modelClientList = new DefaultListModel<>();
        listClientList = new JList(modelClientList);
        listClientList.setEnabled(false);
        listClientList.setSelectedIndex(0);
        scrollPaneClientList.setViewportView(listClientList);
        
        JLabel lblUser = new JLabel("User");
        lblUser.setBounds(10, 61, 46, 14);
        frmTcpClient.getContentPane().add(lblUser);
        
        textFieldUser = new JTextField();
        textFieldUser.setBounds(38, 61, 133, 20);
        frmTcpClient.getContentPane().add(textFieldUser);
        textFieldUser.setColumns(10);
        
        JLabel lblFileList = new JLabel("File List");
        lblFileList.setBounds(295, 160, 133, 14);
        frmTcpClient.getContentPane().add(lblFileList);
        
        btnDownloadFile = new JButton("Download File");
        btnDownloadFile.setEnabled(false);
        btnDownloadFile.setBounds(550, 514, 119, 23);
        frmTcpClient.getContentPane().add(btnDownloadFile);
        
        JLabel lblSharedFolder = new JLabel("Shared Folder:");
        lblSharedFolder.setBounds(10, 92, 89, 14);
        frmTcpClient.getContentPane().add(lblSharedFolder);
        
        textFieldSharedFolder = new JTextField();
        textFieldSharedFolder.setBounds(10, 111, 161, 20);
        frmTcpClient.getContentPane().add(textFieldSharedFolder);
        textFieldSharedFolder.setColumns(10);
        
        btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(181, 110, 110, 23);
        frmTcpClient.getContentPane().add(btnBrowse);
        
        tableModelFileList = new DefaultTableModel();
        tableFileList = new JTable(tableModelFileList);
        tableFileList.setEnabled(false);
        tableFileList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableModelFileList.addColumn("Name");
        tableModelFileList.addColumn("Size");
        tableModelFileList.addColumn("Date");
        tableModelFileList.addColumn("Owner");
        
        JScrollPane scrollPaneFileList = new JScrollPane(tableFileList, 
                                                         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneFileList.setBounds(295, 185, 374, 322);
        scrollPaneFileList.setViewportView(tableFileList);
        frmTcpClient.getContentPane().add(scrollPaneFileList);
        
        btnInitServer = new JButton("Init Server");
        btnInitServer.setBounds(181, 57, 110, 23);
        frmTcpClient.getContentPane().add(btnInitServer);
        
        btnCloseServer = new JButton("Close Server");
        btnCloseServer.setBounds(181, 83, 110, 23);
        frmTcpClient.getContentPane().add(btnCloseServer);
        btnCloseServer.setEnabled(false);
        
        clientConnection = new ClientConnection(this);
                
        createActions();
    }
    
    private int validateConnectionFields()
    {
        int ret = SUCCESS;
        File folder = null;
        
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
        else if (textFieldSharedFolder.getText().length() == 0)
        {
            //Show error message
            JOptionPane.showMessageDialog(null,
                                          "Please type the shared folder",
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            ret = FAIL;
        }
        else
        {
            folder = new File(textFieldSharedFolder.getText());
            
            if (!folder.isDirectory())
            {
                //Show error message
                JOptionPane.showMessageDialog(null,
                                              "Shared folder path does not exist",
                                              "Error", 
                                              JOptionPane.ERROR_MESSAGE);
                ret = FAIL;
            }
        }
        
        return ret;
    }
    
    private int validateFieldsToSend()
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
        
        //Browse
        btnBrowse.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              JFileChooser fileChooser = new JFileChooser();
              
              fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);
          
              fileChooser.setAcceptAllFileFilterUsed(false);
       
              int rVal = fileChooser.showOpenDialog(null);
              
              if (rVal == JFileChooser.APPROVE_OPTION) 
              {
                  textFieldSharedFolder.setText(fileChooser.getSelectedFile().toString());
              }
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
        
        //Download
        btnDownloadFile.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              download();
          }
        });
        
        //Init server
        btnInitServer.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              initServer();
          }
        });
        
        //Close server
        btnCloseServer.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
              closeServer();
          }
        });
    }
    
    private void connect()
    {
        String host = textFieldHost.getText();
        String user = textFieldUser.getText();
        File   sharedFolder = new File(textFieldSharedFolder.getText());
        int    port = -1;
        int    ret = SUCCESS;
        
        if (textFieldPort.getText().length() != 0)
        {
            port = Integer.parseInt(textFieldPort.getText());
        }
        
        ret = clientConnection.initConnection(host, port, user, sharedFolder);
        
        if (ret == SUCCESS)
        {
            //Communication established
                   
            //Turn on the fields
            btnConnect.setEnabled(false);
            btnBrowse.setEnabled(false);
            btnInitServer.setEnabled(false);
            btnCloseServer.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);
            btnSendToAll.setEnabled(true);
            btnDownloadFile.setEnabled(true);
            listClientList.setEnabled(true);
            tableFileList.setEnabled(true);
                    
            textAreaMessSent.setEnabled(true);
            textAreaMessReceived.setEnabled(true);
                    
            textFieldHost.setEditable(false);
            textFieldPort.setEditable(false);
            textFieldUser.setEditable(false);
            textFieldSharedFolder.setEditable(false);
                    
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
    
    private void disconnect()
    {
        clientConnection.stopConnection();
        
        //Turn off the fields
        btnBrowse.setEnabled(true);
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnInitServer.setEnabled(true);
        btnSend.setEnabled(false);
        btnSendToAll.setEnabled(false);
        btnDownloadFile.setEnabled(false);
        listClientList.setEnabled(false);
        tableFileList.setEnabled(false);
        
        textAreaMessSent.setEnabled(false);
        textAreaMessReceived.setEnabled(false);
        
        textFieldHost.setEditable(true);
        textFieldPort.setEditable(true);  
        textFieldUser.setEditable(true);
        textFieldSharedFolder.setEditable(true);
    }
    
    private void send()
    {
        ArrayList <String> destination = null;
        
        destination = getSelectedClientList();
        
        if (destination != null)
        {
            clientConnection.sendMessage(textAreaMessSent.getText(), destination);
        
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
    
    private void sendToAll()
    {
        clientConnection.sendMessageToAll(textAreaMessSent.getText());
        textAreaMessSent.setText("");
    }
    
    private int download()
    {
        int ret = SUCCESS;
        FileList file = null;
        
        file = getSelectedFileList();
        
        if (file != null)
        {
            ret = clientConnection.downloadFile(file);
            
            if (ret == FILE_ALR_DOWN)
            {
                //Show error message
                JOptionPane.showMessageDialog(null,
                                              "This file is local",
                                              "Error", 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            //Show error message
            JOptionPane.showMessageDialog(null,
                                          "Please select a file to download",
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            
        }
        
        return ret;
    }
    
    public void updateMessReceived(String str)
    {
        if (str != null)
        {
            textAreaMessReceived.append(str + "\n");
        }
    }
    
    public void updateClientList(ArrayList <String>list)
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
    
    private ArrayList<String> getSelectedClientList()
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
    
    private void removeAllRowsFileList()
    {
        if (tableModelFileList != null)
        {
            for (int i = tableModelFileList.getRowCount() - 1; i >= 0; i--)
            {
                tableModelFileList.removeRow(i);
            }
        }
    }
    
    public void updateFileList(ArrayList <FileList>list)
    {
        if (list != null)
        {
            FileList file = null;
            removeAllRowsFileList();
            
            for (int i = 0; i < list.size(); i++)
            {
                file = list.get(i);
                
                if (file != null)
                {
                    tableModelFileList.addRow(new Object[]{
                                              file.getFileName(),
                                              "" + file.getSize(),
                                              file.getDate(),
                                              file.getOwner()});
                }
            }
        }
        else
        {
            removeAllRowsFileList();
        }
    }
    
    private FileList getSelectedFileList()
    {
        FileList file = null;
        int row = tableFileList.getSelectedRow();

        if (row > 0)
        {
            file = clientConnection.getFileListFromIndex(row);
        }
        
        return file;
    }
    
    public void showDownloadMessageDialog()
    {
        JOptionPane.showMessageDialog(null,
                                      "File successfully downloaded",
                                      "", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void initServer()
    {
        if (clientConnection != null)
        {
            int ret = SUCCESS;
            
            ret = clientConnection.initServer();
            
            if (ret == SUCCESS)
            {
                updateMessReceived("Server online");
                btnCloseServer.setEnabled(true);
                btnInitServer.setEnabled(false);
                serverOnline = true;
            }
            else
            {
                updateMessReceived("Server cannot be opened");
            }
        }
    }
    
    public void closeServer()
    {
        if (serverOnline
            && clientConnection != null)
        {
            clientConnection.stopServer();
            updateMessReceived("Server closed");
            btnCloseServer.setEnabled(false);
            btnInitServer.setEnabled(true);
            serverOnline = false;
        }
    }
}
