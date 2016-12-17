/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.util.ArrayList;

/**
 *
 * @author Stephan Wolfgang Kusch 
 */
public class TeilnehmerListe {
    
    private ArrayList <Teilnehmer> clients ;
    private int clientNum = 0;
    
    public void Teilnehmerliste(){
        
        clients = new ArrayList <Teilnehmer> ();
    }
    
    public synchronized void addTeilnehmer(Teilnehmer client){
        
        clients.add(client);
        clientNum ++;     
    }
    
    public synchronized void remove(int i){
        
        clients.remove(i);
        clientNum --;
    }
    
    public synchronized Teilnehmer get(int i){
        
        return clients.get(i);
    }
    
    public synchronized int getSize(){
        
        return clientNum;
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
    
    private void commandMassage(){
        
        
    }
    
    private void commandNamelist(){
        
        
    }
    
    private void commandDisconnect(){
        
        
    }
    
    public synchronized void sendMessage(String message, String name){
        
        
    }
}
