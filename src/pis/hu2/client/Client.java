package pis.hu2.client;

import pis.hu2.common.Message;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Kühn, Konstantin
 * @matrikelnummer 5060992
 * @hausübung PIS Hausübung 2
 *
 * @klasseninvariante Die Variablen <code>VerbundeneNutzer</code> und <code>stapel</code> sind zu
 * keiner Zeit null. Der Socket <code>s</code> ist nach dem Aufruf von run() zu keiner
 * Zeit null.
 */
public class Client implements Runnable {
    private String userName;
    private String hostIP;
    private int hostPort;
    private boolean neueEintraege = false;
    private Socket s;
    private StringBuilder stapel = new StringBuilder();

    private  ArrayList<String> VerbundeneNutzer = new ArrayList<String>();

    /**
     * Client Konstruktor mit Standard Werten
     */
    public Client() {
        this.userName = "Client";
        this.hostIP = "127.0.0.1";
        this.hostPort = 7575;
    }

    /**
     * Variabler Client Konstruktor, bei dem Nutzerspezifische Daten eingesetzt werden
     * @Bedingung Alle übergebene Objekte dürfen nicht null sein
     * @param userName
     * @param hostIP
     * @param hostPort
     */
    public Client(String userName, String hostIP, int hostPort) {
        this.userName = userName;
        this.hostIP = hostIP;
        this.hostPort = hostPort;
    }
    public String getUserName() {

        return userName;
    }

    public synchronized boolean isNeueEintraege()
    {
        return neueEintraege;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }


    public StringBuilder getStapel() {

        return stapel;
    }

    public ArrayList<String> getVerbundene_clients()
    {
        return VerbundeneNutzer;
    }



    public synchronized void setNeueEintraege(boolean neueEintraege) {

        this.neueEintraege = neueEintraege;
    }


    public Socket getS() {
        return s;
    }
    public void setHostIP(String hostIP)
    {
        this.hostIP = hostIP;
    }

    public void setHostPort(int hostPort) {

        this.hostPort = hostPort;
    }

    /**
     * Überprüft ob der Client erfolgreich mit dem Socket verbunden ist.
     * @return true = isClosed
     */
    public  boolean isclosed(){
        boolean tmp = true;
        try{
       tmp = s.isClosed();
        }catch(NullPointerException nE){

        }
        return tmp;
    }
    /**
     * Verbindungsaufbau, Nachrichtenempfang
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            s = new Socket(hostIP, hostPort);
            this.sendeNachricht("connect:" + this.getUserName());
            BufferedReader vomServer = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            String read_line = null;
            // Verarbeitet die eingehenden Nachrichten
            while (!s.isClosed()) {
                read_line = vomServer.readLine();
                if (read_line != null) {

                    Message msgFromServer = new Message(read_line);
                    System.out.println(msgFromServer.getMessageAsString());
                    String command = msgFromServer.getMessageAsStringArray()[0];
                    String message = msgFromServer.getMessageAsStringArray()[1];
                    switch (command) {
                        case "refused":
                            switch (message) {
                                case "too_many_users":
                                    stapel.append("Keine Verbindung möglich, zuviele Nutzer online!" + "\n");

                                    break;

                                case "name_in_use":
                                    stapel.append("Keine Verbidung möglich, der gewählte Benutzername ist leider schon vergeben!" + "\n");
                                    break;

                                case "invalid_name":
                                    stapel.append("Keine Verbindung möglich, der gewählte Benutzername ist ungültig!" + "\n");
                                    break;

                                default:
                            }
                            break;

                        case "connect":
                            if (message.equals("ok")) {
                                stapel.append("Eine Verbindung zum Server wurde erfolgreich hergestellt!"
                                        + "\n");
                            }
                            break;

                        case "disconnect":
                            switch (message) {
                                case "ok":
                                case "invalid_command":
                                    stapel.append("Verbindung zum Server wurde unterbrochen!" + "\n");
                                    VerbundeneNutzer.clear();
                                    s.close();
                                    break;

                                default:

                            }
                            break;

                        case "message":
                            stapel.append(message + "\n");
                            break;

                        case "namelist":
                            VerbundeneNutzer.clear();
                            for(String u:msgFromServer.getAllUserNames()){
                                VerbundeneNutzer.add(u);
                            }
                            break;

                        default:
                    }
                    this.setNeueEintraege(true);
                    message = null;
                }
            }
            s.close();
            vomServer.close();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException ioe) {
            stapel.append("Keine Verbindung zum Server möglich!"+"\n");
            this.setNeueEintraege(true);
        }
    }

    /**
     * Diese Klasse ermöglicht es Nachirchten zu versenden.
     *
     * @precondition Das übergebene Objekt darf nicht NULL sein!
     * @param msg
     *            die zu versendene Nachricht
     * @throws IOException
     */
    public void sendeNachricht(String msg) throws IOException {
        if(s != null){
            if (!s.isClosed()) {
                PrintWriter socket_out = new PrintWriter(new OutputStreamWriter(
                        s.getOutputStream()));

                Message msgout = new Message(msg);
                String cmd = msgout.getMessageAsStringArray()[0];

                switch (cmd) {
                    case "connect":
                    case "message":
                    case "disconnect": {
                        socket_out.write(msgout.getMessageAsString() + "\n");
                        break;
                    }
                    default: {
                        Message dftMsg = new Message("message",msg);
                        socket_out.write(dftMsg.getMessageAsString() + "\n");
                    }
                }
                socket_out.flush();
            }
        }
    }
}