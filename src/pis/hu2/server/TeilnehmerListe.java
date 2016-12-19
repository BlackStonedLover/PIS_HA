/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Stephan Wolfgang Kusch 
 */
public class TeilnehmerListe {
    
    private ArrayList <Teilnehmer> clients ;
    private int client_Num = 0;
    
    public void Teilnehmerliste(){
        
        clients = new ArrayList <Teilnehmer> ();
    }
    
    public synchronized void addTeilnehmer(Teilnehmer client){
        
        clients.add(client);
        client_Num ++;     
    }
    
    public synchronized void remove(int i){
        
        clients.remove(i);
        client_Num --;
    }
    
    public synchronized Teilnehmer get(int i){
        
        return clients.get(i);
    }
    
    public synchronized int getSize(){
        
        return client_Num;
    }
    
    public synchronized int findClient (Teilnehmer client){
        
        int counter = 0;
        for (Teilnehmer gesucht : clients){
            
            if(gesucht.equals(client)){
                
                return counter;
            } else {
                
                counter ++;
            }
        }
        return -1;
    }
    
    public synchronized ArrayList<String> aktuellConnected(){
        
        ArrayList<String> connectedClients = new ArrayList<>();
        for(Teilnehmer client : clients){
            
            connectedClients.add(client.getName());
        }
        return connectedClients;
    }
    
    public synchronized void sendMessage(String msg, String name){
        
        String command = null;
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
                        if (!already_sent){
                            
                            Server.getLog().append(name).append(":").append(message_cut).append("\n");
                        }
                        break;
                    }
                    case "namelist:": {
                        
                        socket_out.write(msg + "\n");
                        if (!already_sent){
                            
                            Server.getLog().append(msg + "\n");
                        }
                        break;
                    }
                    case "disconnect:": {
                        
                        socket_out.write("disconnect:ok" + "\n");
                        if (!already_sent){
                            
                            Server.getLog().append("Die Verbindung aller Teilnehmer wurde beendet!\n");
                        }
                        break;
                    }
                    default:
                        
                        socket_out.write("message:" + name + ":" + msg + "\n");
                        if (!already_sent) {
                            
                            Server.getLog().append(name).append(": ").append(msg).append("\n");
                        }
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
                client_Num = 0;
            }
        }  
    }
}
