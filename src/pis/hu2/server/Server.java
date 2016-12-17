/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author CKC
 */
public class Server implements Runnable {
    
    static boolean on = true;
    private int port;
    private ServerSocket serverSocket;
    private TeilnehmerListe clients;
    private static boolean changed = false;
    private static StringBuilder log = new StringBuilder();
    
    public Server(){

    }

    @Override
    public void run() {

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
