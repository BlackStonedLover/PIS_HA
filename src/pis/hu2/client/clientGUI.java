package pis.hu2.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Kühn, Konstantin
 * @matrikelnummer 5060992
 * @hausübung PIS Hausübung 2
 */
@SuppressWarnings("serial")
public class ClientGUI extends JFrame implements ActionListener,
        WindowListener, Runnable {
    private JTextField userNameField;
    private JTextField ipAdressField;
    private JTextArea messageArea;
    private JTextField portTargetfield;
    private JTextField messageField;
    private Client client;
    private Thread client_Thread;
    private DefaultListModel<String> defualtListModel;
    private JButton connectionButton;


    /**
     * Erzeugt eine Grafische Oberfläche und verarbeitet die von der Client
     * Schnittstelle  übergebenen Client-Objekten und damit die Kommunikation mit
     * dem Server.
     *
     * @precondition Das übergebene Objekt darf nicht NULL sein!
     * @param client
     */
    public ClientGUI(Client client) {
        super("Chat Client");

        this.client = client;
        this.addWindowListener(this);

        // top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel(" Server:"));
        ipAdressField = new JTextField("127.0.0.1", 10);
        topPanel.add(ipAdressField);
        topPanel.add(new JLabel(" Port:"));
        portTargetfield = new JTextField("7575", 4);
        topPanel.add(portTargetfield);
        topPanel.add(new JLabel(" User:"));
        userNameField = new JTextField("User1", 8);
        topPanel.add(userNameField);
        connectionButton = new JButton("Connect");
        connectionButton.setActionCommand("connect");
        connectionButton.addActionListener(this);
        topPanel.add(connectionButton);


        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        // list of users
        defualtListModel = new DefaultListModel<String>();
        JList<String> users = new JList<String>(defualtListModel);
        users.setPrototypeCellValue("XXXXXXXXXXXXXXXX");

        // text field for typing new messageArea
        messageField = new JTextField();
        messageField.setActionCommand("send");
        messageField.addActionListener(this);

        // Frame
        add(topPanel, "North");
        add(new JScrollPane(users), "East");
        add(new JScrollPane(messageArea), "Center");
        add(messageField, "South");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    /**
     * Button Handling
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            String command = e.getActionCommand();
            client_Thread = new Thread(client);
            if (command.equals("connect")) {
                client.setUserName(userNameField.getText());
                client.setHostIP(ipAdressField.getText());
                client.setHostPort(Integer.valueOf(portTargetfield.getText()));
                if (!client_Thread.isAlive()) {
                    client_Thread.start();

                }
                if(!client.isclosed()) {
                    connectionButton.setText("Disconnect");
                    connectionButton.setActionCommand("disconnect");
                }
            } else if (command.equals("send")) {
                if (messageField.getText().equals("disconnect:")) {
                    connectionButton.setText("Connect");
                    connectionButton.setActionCommand("connect");
                    client_Thread.interrupt();
                    client.sendeNachricht("disconnect:");
                } else {
                    client.sendeNachricht(messageField.getText());
                }
                messageField.setText("");
            } else if (command.equals("disconnect")) {
                connectionButton.setText("Connect");
                connectionButton.setActionCommand("connect");
                client.sendeNachricht("disconnect:");
                client_Thread.interrupt();
                messageField.setText("");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        do {
            if (client.isNeueEintraege()) {
                messageArea.setText(String.valueOf(client.getStapel()));
                updateUserList(client.getVerbundene_clients());
                client.setNeueEintraege(false);
            }
            try {
                Thread.sleep(250); // Der Thread prüft dadurch nicht "ununterbrochen" ob etwas geändert wurde.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    /**
     * Aktualisiert das verwendete ListModel anhand der zur Zeit verbundenen
     * Clients.
     *
     * @precondition Das übergebene Objekt darf nicht NULL sein!
     * @param verbundene_clients
     *            Liste der verbundenen Clients
     */
    private void updateUserList(ArrayList<String> verbundene_clients) {
        defualtListModel.clear();
        for (int i = 0; i < client.getVerbundene_clients().size(); i++) {
            defualtListModel.add(i, client.getVerbundene_clients().get(i));
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent e) {
        // Ermöglicht ein sauberes Beenden des Clients durch den Schließen-Knopf
        try {
            if (client.getS() != null) {
                client.sendeNachricht("disconnect:");
                client_Thread.interrupt();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
