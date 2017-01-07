/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Stephan Wolfgang Kusch
 * @matrikelnummer 5096996
 * 
 * Die Variablen <code>clients</code> und <code>log</code> sind nicht null.
 * Die Variable <code>serverSocket</code> wird gesetzt beim Starten des Threads.
 */
public class Server implements Runnable {
    
    private int m_Port;
    private ServerSocket m_ServerSocket;
    private TeilnehmerListe m_Clients;
    private static boolean changed = false;
    private static StringBuilder log = new StringBuilder();
    
    /**
    * Server-Konstruktor mit Werten zur Initialisierung der Variablen.
    */

    public Server(){
        
        this.m_Clients = new TeilnehmerListe();
        this.m_Port = 7575;
    }
    
    /**
     * Server-Konstruktor bei dem man die Werte bei der Generierung selbst festlegen kann.
     * @param port
     */
    
    public Server(int port) {
        
        this.m_Port = port;
        this.m_Clients = new TeilnehmerListe();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */

    @Override
    public void run() {
        
        try {
            
            m_ServerSocket = new ServerSocket(m_Port);
            // Wartet auf eingehende Verbindungen und erzeugt für jede, einen einzelnen Thread.
            while (!m_ServerSocket.isClosed()) {
                
                log.append("Warte auf eingehende Verbindung..." + "\n");
                changed = true;
                Socket clientSocket = m_ServerSocket.accept();
                if (m_Clients.getSize() < 3) {
                    
                    Teilnehmer handler = new Teilnehmer(clientSocket, m_Clients);
                    new Thread(handler).start();
                } else {
                    
                    PrintWriter socket_out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    socket_out.write("refused:too_many_users" + "\n");
                    socket_out.flush();
                    socket_out.close();
                }
            }
        } catch (IOException e) {
            
            log.append("Server wurde während dem Warten auf eine Verbindung gestoppt!" + "\n");
            changed = true;
        }
    }

    public synchronized void setPort(int port) {
        
        this.m_Port = port;
    }

    public synchronized TeilnehmerListe getClients() {
        
        return m_Clients;
    }

    public synchronized ServerSocket getServerSocket() {
        
        return m_ServerSocket;
    }

    public synchronized static StringBuilder getLog() {
        
        return log;
    }

    public synchronized boolean isChanged() {
        
        return changed;
    }

    public synchronized static void setChanged(boolean changed) {
        
        Server.changed = changed;
    }
}