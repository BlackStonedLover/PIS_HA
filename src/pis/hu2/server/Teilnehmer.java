/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.net.Socket;

/**
 *
 * @author Stephan Wolfgang Kusch
 */
public class Teilnehmer implements Runnable {
    
    private final Socket m_Socket;
    private String m_Name;
    private final TeilnehmerListe m_ClientList;
    
    public Teilnehmer(Socket socket, TeilnehmerListe clientList){
        
        this.m_Socket = socket;
        this.m_ClientList = clientList;
    }

    @Override
    public void run() {
        
        
    }
    
    public synchronized Socket getSocket(){
        
        return m_Socket;
    }

    public synchronized String getName(){
        
        return m_Name;
    }
    
    private synchronized boolean isNameUsed(String name){
        
        for (int i = 0; i < m_ClientList.getSize(); i++)
        {
            if (m_ClientList.get(i).getName().equals(name))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private synchronized boolean isNameCorrect(String name){
        
        return !(name.length() > 30 || name.length() == 0 || name.contains(":"));
    }
    
    public synchronized String getNamelist(){
        
        
    }
    
    
    
    private boolean commandConnect(){
        
        
    }
    
    private boolean commandDisconnect(){
        
        
    }
    
    private void commandDefault(){
        
        
    }
    
    private void processMessage(){
        
        
    }
}
