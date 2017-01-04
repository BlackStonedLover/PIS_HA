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
    private JTextField ipTextField;
    private JTextField portTextField;
    private JButton startButton;
    private Server server;
    private JTextArea messages;
    private DefaultListModel<String> defMod;
    private JButton stopButton;
    private JTextField messageTextField;
    private Thread server_Thread;

    /**
     * Erzeugt die grafische Oberfläche und verarbeitet über die Server Schnittstelle mit dem
     * übergebenen Server-Objekten die Kommunikation mit den Clients.
     * 
     * @precondition Das übergebene Objekt darf nicht NULL sein!
     * @param server
     */
    public ServerGUI(Server server) {
        super("Server");
        this.server = server;
        this.addWindowListener(this);

        // top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel(" IP:"));
        ipTextField = new JTextField("", 10);
        ipTextField.setEnabled(false);
        portTextField = new JTextField("7575", 4);
        topPanel.add(ipTextField);
        topPanel.add(new JLabel(" Port:"));
        topPanel.add(portTextField);
        startButton = new JButton("Start Server");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        topPanel.add(startButton);

        stopButton = new JButton("Stop Server");
        stopButton.setActionCommand("stop");
        stopButton.addActionListener(this);
        topPanel.add(stopButton);

        // server-log
        messages = new JTextArea();
        messages.setEditable(false);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);

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
        add(new JScrollPane(messages), "Center");
        add(messageTextField, "South"); // "South" == BorderLayout.SOUTH
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        setVisible(true);

    }

    /** (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        
        String command = e.getActionCommand();
        server_Thread = new Thread(server);
        if (command.equals("start")) {
            
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            server.setPort(Integer.valueOf(portTextField.getText()));
            if(!server_Thread.isAlive()){

                server_Thread.start();
            }
        } else if (command.equals("stop")) {

            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            server.getClients().sendMessage("disconnect:ok", "Server");
            server_Thread.interrupt();
            try {

                server.getServerSocket().close();
            } catch (IOException e1) {

                e1.printStackTrace();
            }
        } else if (command.equals("send")) {
            
            if(messageTextField.getText().equals("disconnect:")){
                
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                server.getClients().sendMessage("disconnect:ok", "Server");
                server_Thread.interrupt();
                try {
                    
                    server.getServerSocket().close();
                } catch (IOException e1) {
                    
                    e1.printStackTrace();
                }
            } else {
                
                server.getClients().sendMessage(messageTextField.getText(), "Server");
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

            if(server.isChanged()) {

                messages.setText(String.valueOf(Server.getLog()));
                updateListModel(server.getClients().isConnected());
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
        for(int i = 0; i < server.getClients().isConnected().size(); i++){

            defMod.add(i, server.getClients().isConnected().get(i));
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
            
            if(server.getServerSocket() != null){
                
                server.getClients().sendMessage("disconnect:ok", "Server");
                server_Thread.interrupt();
                server.getServerSocket().close();
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
