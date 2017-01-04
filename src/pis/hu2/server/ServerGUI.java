/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class ServerGUI extends JFrame implements ActionListener, WindowListener, Runnable {
    
    private JTextField portTextField;
    private JButton m_OnOffButton;
    private Server m_Server;
    private JTextArea m_Messages;
    private DefaultListModel<String> defMod;
    private JTextField messageTextField;
    private Thread m_ServerThread;

    /**
     * Erzeugt die grafische Oberfläche und verarbeitet über die Server Schnittstelle mit dem
     * übergebenen Server-Objekten die Kommunikation mit den Clients.
     * 
     * @precondition Das übergebene Objekt darf nicht NULL sein!
     * @param server
     */
    public ServerGUI(Server server) throws UnknownHostException {
        super("Server");
        this.m_Server = server;
        this.addWindowListener(this);

        // top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel(" IP :  "+ Inet4Address.getLocalHost().getHostAddress()));
        portTextField = new JTextField("7575", 4);
        topPanel.add(new JLabel(" Port:"));
        topPanel.add(portTextField);
        m_OnOffButton = new JButton("Start Server");
        m_OnOffButton.setActionCommand("start");
        m_OnOffButton.addActionListener(this);
        topPanel.add(m_OnOffButton);

        // server-log
        m_Messages = new JTextArea();
        m_Messages.setEditable(false);
        m_Messages.setLineWrap(true);
        m_Messages.setWrapStyleWord(true);

        // list of users
        defMod = new DefaultListModel<String>();
        JList<String> users = new JList<String>(defMod);
        users.setPrototypeCellValue("XXXXXXXXXXXXXXXX");

        // text field for typing new messages and broadcast them
        messageTextField = new JTextField();
        messageTextField.setActionCommand("send");
        messageTextField.addActionListener(this);


        // Frame
        add(topPanel, "North");
        add(new JScrollPane(users), "West");
        add(new JScrollPane(m_Messages), "Center");
        add(messageTextField, "South"); // "South" == BorderLayout.SOUTH
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        setVisible(true);

    }

    /** (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        
        String m_Command = e.getActionCommand();
        m_ServerThread = new Thread(m_Server);
        if (m_Command.equals("start")) {
            
            m_OnOffButton.setText("Stop Server");
            m_OnOffButton.setActionCommand("stop");
            m_Server.setPort(Integer.valueOf(portTextField.getText()));
            if(!m_ServerThread.isAlive()){

                m_ServerThread.start();
            }
        } else if (m_Command.equals("stop")) {

            m_OnOffButton.setText("Start Server");
            m_OnOffButton.setActionCommand("start");
            m_Server.getClients().sendMessage("disconnect:ok", "Server");
            m_ServerThread.interrupt();
            try {

                m_Server.getServerSocket().close();
            } catch (IOException e1) {

                e1.printStackTrace();
            }
        } else if (m_Command.equals("send")) {
            
            if(messageTextField.getText().equals("disconnect:")){
                
                m_OnOffButton.setText("Start Server");
                m_OnOffButton.setActionCommand("start");
                m_Server.getClients().sendMessage("disconnect:ok", "Server");
                m_ServerThread.interrupt();
                try {
                    
                    m_Server.getServerSocket().close();
                } catch (IOException e1) {
                    
                    e1.printStackTrace();
                }
            } else {
                
                m_Server.getClients().sendMessage(messageTextField.getText(), "Server");
            }
            messageTextField.setText("");
        }

    }

    /** (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        do {

            if(m_Server.isChanged()) {

                m_Messages.setText(String.valueOf(Server.getLog()));
                updateListModel(m_Server.getClients().isConnected());
                Server.setChanged(false);
            }
            try {

                Thread.sleep(250); // Der Thread prüft dadurch nicht "ununterbrochen" ob etwas geändert wurde.
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        } while(true);
    }

    /**
     * Aktualisiert das verwendete ListModel anhand der zur Zeit verbundenen
     * Clients.
     * 
     * @precondition Das übergebene Objekt darf nicht NULL sein
     * @param verbundene_clients
     *            Liste der verbundenen Clients
     */
    private void updateListModel(ArrayList<String> verbundene_clients){

        defMod.clear();
        for(int i = 0; i < m_Server.getClients().isConnected().size(); i++){

            defMod.add(i, m_Server.getClients().isConnected().get(i));
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    /** (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent e) {
        // Ermöglicht ein sauberes Beenden des Servers durch den Schließen-Knopf
        try {
            
            if(m_Server.getServerSocket() != null){
                
                m_Server.getClients().sendMessage("disconnect:ok", "Server");
                m_ServerThread.interrupt();
                m_Server.getServerSocket().close();
            }
        } catch (IOException e1) {
            
                e1.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
