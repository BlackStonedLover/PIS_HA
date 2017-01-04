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
import java.util.List;


/**
 *
 * @author Stephan Wolfgang Kusch 
 * @klasseninvariante Die Variable <code>clients</code> ist nicht null.
 * Der Wert von <code>client_num</code> befindet sich immer im Bereich von
 * 0 <= client_num <= clients.size() und gibt die aktuelle Anzahl an vorhandenen
 * Teilnehmern an.
 */

public class TeilnehmerListe {

    private volatile List<Teilnehmer> clients;
    private int client_num = 0;

    /**
     * TeilnehmerListe-Konstruktor mit Werten zur Initialisierung der Variablen.
     */
    public TeilnehmerListe() {
        
        clients = new ArrayList<Teilnehmer>();
    }

    /**
     * Ermöglicht das Hinzufügen eines Teilnehmers vom Typ client zu der Liste.
     * 
     * @precondition Das übergebene Objekt darf nicht NULL sein
     * @param client
     *            Objekt welches hinzugefügt werden soll
     */
    public synchronized void add(Teilnehmer client) {
        
        client_num++;
        clients.add(client);
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
        
        return clients.get(i);
    }

    /**
     * Entfernt den Teilnehmer an Position i.
     * 
     * @precondition Der übergebene Wert muss im Bereich (0 <= i <= Anzahl Teilnehmer) liegen
     * @param i
     *            Position des Teilnehmers in der Liste
     */
    public synchronized void remove(int i) {
        
        client_num--;
        clients.remove(i);
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
        for (Teilnehmer gesucht : clients) {

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
        
        String command = null;
        /* @param already_sent
         * Wird benötigt, damit die Nachricht nicht, für die Anzahl
         * der vorhandenen Teilnehmern, in den Server-Log geschrieben wird.
         */
        boolean already_sent = false; 
        for (Teilnehmer client : clients) {
            
            try {
                
                PrintWriter socket_out = new PrintWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
                if (msg.contains(":")) {
                    
                    command = msg.substring(0, msg.indexOf(':') + 1);
                } else {
                    
                    command = msg;
                }
                switch (command) {

                    case "message:": {

                        String message_cut = msg.substring(msg.indexOf(':') + 1, msg.length());
                        socket_out.write("message:" + name + ":" + message_cut + "\n");
                        if (!already_sent)
                            
                                Server.getLog().append(name).append(":").append(message_cut).append("\n");
                        break;
                    }
                    case "namelist:": {
                        
                        socket_out.write(msg + "\n");
                        if (!already_sent)
                            
                            Server.getLog().append(msg).append("\n");
                        break;
                    }
                    case "disconnect:": {
                        
                        socket_out.write("disconnect:ok" + "\n");
                        if (!already_sent)
                            
                            Server.getLog().append("Die Verbindung aller Teilnehmer wurde beendet!" + "\n");
                        break;
                    }
                    default:
                        
                        socket_out.write("message:" + name + ":" + msg + "\n");
                        if (!already_sent) Server.getLog().append(name).append(": ").append(msg).append("\n");
                }
                already_sent = true;
                Server.setChanged(true);
                socket_out.flush();
                if(command.equals("disconnect:")){

                    client.getSocket().close();
                }
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
        if (command != null) {
            
            if (command.equals("disconnect:")) {
                
                clients.clear();
                client_num = 0;
            }
        }
    }

    /**
     * Gibt die Anzahl der in der Teilnehmerliste befindlichen Nutzer zurück.
     * 
     * @return Anzahl der Teilnehmer
     */
    public synchronized int getSize() {
        
        return client_num;
    }

    /**
     * Erzeugt eine Liste von den verbundenen Teilnehmern und gibt diese zurück.
     * 
     * @return Verbundene Teilnehmer
     */
    public synchronized ArrayList<String> isConnected() {
        
        ArrayList<String> connected_clients = new ArrayList<String>();
        for (Teilnehmer client : clients) {
            
            connected_clients.add(client.getName());
        }
        return connected_clients;
    }
}
