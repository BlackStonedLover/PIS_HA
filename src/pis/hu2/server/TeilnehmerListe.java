/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pis.hu2.common.Message;


/**
 *
 * @author Stephan Wolfgang Kusch 
 * @matrikelnummer 5096996

 * @klasseninvariante Die Variable <code>clients</code> ist nicht null.
 * Der Wert von <code>client_num</code> befindet sich immer im Bereich von
 * 0 <= client_num <= clients.size() und gibt die aktuelle Anzahl an vorhandenen
 * Teilnehmern an.
 */

public class TeilnehmerListe {

    private volatile List<Teilnehmer> m_Clients;
    private int m_ClientNum = 0;

    /**
     * TeilnehmerListe-Konstruktor mit Werten zur Initialisierung der Variablen.
     */
    public TeilnehmerListe() {
        
        m_Clients = new ArrayList<Teilnehmer>();
    }

    /**
     * Ermöglicht das Hinzufügen eines Teilnehmers vom Typ client zu der Liste.
     * 
     * @precondition Das übergebene Objekt darf nicht NULL sein
     * @param client
     *            Objekt welches hinzugefügt werden soll
     */
    public synchronized void add(Teilnehmer client) {
        
        m_ClientNum++;
        m_Clients.add(client);
    }

    /**
     * Ermöglicht den Zugriff auf einen Teilnehmer in der Liste an Position i.
     * 
     * @precondition Der übergebene Wert muss im Bereich (0 <= i <= Anzahl Teilnehmer) liegen
     * @param i
     *            Position des Teilnehmers in der Liste
     * @return Gibt einen Teilnehmer zurück
     */
    public synchronized Teilnehmer get(int i) {
        
        return m_Clients.get(i);
    }

    /**
     * Entfernt den Teilnehmer an Position i.
     * 
     * @precondition Der übergebene Wert muss im Bereich (0 <= i <= Anzahl Teilnehmer) liegen
     * @param i
     *            Position des Teilnehmers in der Liste
     */
    public synchronized void remove(int i) {
        
        m_ClientNum--;
        m_Clients.remove(i);
    }

    /**
     * Sucht einen bestimmten Teilnehmer in der Liste.
     * 
     * @precondition Das übergebene Objekt darf nicht NULL sein!
     * @param client
     *            Zu suchender Teilnehmer
     * @return Gibt die Position des gesuchten Teilnehmers zurück, sonst -1
     */
    public synchronized int findClient(Teilnehmer client) {
        
        int counter = 0;
        for (Teilnehmer gesucht : m_Clients) {

            if (gesucht.equals(client)) {

                return counter;
            } else {

                counter++;
            }
        }
        return -1;
    }

    /**
     * Ermoglicht das Senden einer Nachricht an die in der Teilnehmerliste
     * befindlichen Nutzer.
     * 
     * @precondition Die übergebenen Objekte dürfen nicht NULL sein!
     * @param msg
     *            Zu übersendende Nachricht
     * @param name
     *            Name des Senders
     */
    public synchronized void sendMessage(String msg, String name) {
        
        String m_Command = null;
        /* @param already_sent
         * Wird benötigt, damit die Nachricht nicht, für die Anzahl
         * der vorhandenen Teilnehmern, in den Server-Log geschrieben wird.
         */
        boolean is_sent = false; 
        

        
        for (Teilnehmer client : m_Clients) {
            
            try {
                
                PrintWriter socket_out = new PrintWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
                Message msgIn = new Message(msg);
                m_Command = msgIn.getMessageAsStringArray()[0];
                String message = msgIn.getMessageAsStringArray()[1];

                switch (m_Command) {

                    case "message": {
                        
                        Message msgOut = new Message("message", name, message);
                        socket_out.write( msgOut.getMessageAsString() + "\n");
                        if (!is_sent)
                            
                            Server.getLog().append(msgOut.getMessageAsStringArray()[1]).append("\n");
                        break;
                    }
                    case "namelist": {
                        
                        socket_out.write(msg + "\n");
                        if (!is_sent)
                            
                            Server.getLog().append(msg).append("\n");
                        break;
                    }
                    case "disconnect": {
                        
                        socket_out.write("disconnect:ok" + "\n");
                        if (!is_sent)
                            
                            Server.getLog().append("Die Verbindung aller Teilnehmer wurde beendet!" + "\n");
                        break;
                    }
                    default:
                        
                        Message msgOut = new Message ("message" , name, msg);
                        socket_out.write(msgOut.getMessageAsString());
                        if (!is_sent) 
                            Server.getLog().append(msg);
                }
                is_sent = true;
                Server.setChanged(true);
                socket_out.flush();
                if(m_Command.equals("disconnect:")){

                    client.getSocket().close();
                }
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
        if (m_Command != null) {
            
            if (m_Command.equals("disconnect:")) {
                
                m_Clients.clear();
                m_ClientNum = 0;
            }
        }
    }

    /**
     * Gibt die Anzahl der Nutzer in der Teilnehmerliste zurück.
     * 
     * @return Anzahl der Teilnehmer
     */
    public synchronized int getSize() {
        
        return m_ClientNum;
    }

    /**
     * Erzeugt eine Liste von den verbundenen Teilnehmern und gibt diese zurück.
     * 
     * @return Verbundene Teilnehmer
     */
    public synchronized ArrayList<String> isConnected() {
        
        ArrayList<String> connected_clients = new ArrayList<String>();
        for (Teilnehmer client : m_Clients) {
            
            connected_clients.add(client.getName());
        }
        return connected_clients;
    }
}
