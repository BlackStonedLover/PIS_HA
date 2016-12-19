/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Stephan Wolfgang Kusch
 */
public class Server implements Runnable {
    
    static boolean on = true;
    private int port;
    private ServerSocket serverSocket;
    private TeilnehmerListe clients;
    private static boolean changed = false;
    private static StringBuilder log = new StringBuilder();
    
    public Server(){
        
        this.clients = new TeilnehmerListe();
        this.port = 7575;
    }

    @Override
    public void run() {
        
        try {

            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()) {

                log.append("Warte auf eingehende Verbindung..." + "\n");
                changed = true;
                Socket clientSocket = serverSocket.accept();
                if (clients.getSize() < 3) {

                    Teilnehmer handler = new Teilnehmer(clientSocket, clients);
                    new Thread(handler).start();
                } else {

                    try (PrintWriter socket_out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                        socket_out.write("refused:too_many_users" + "\n");
                        socket_out.flush();
                    }
                }
            }
        } catch (IOException e) {

            log.append("Server wurde während dem Warten auf eine Verbindung gestoppt!" + "\n");
            changed = true;
        }
    }
    
    public synchronized void setPort(int port) {
        
        this.port = port;
    }

    public synchronized TeilnehmerListe getClients() {
        
        return clients;
    }

    public synchronized ServerSocket getServerSocket() {
        
        return serverSocket;
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
