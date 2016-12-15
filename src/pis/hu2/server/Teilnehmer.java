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
public class Teilnehmer {
    
    private Socket socket;
    private String name;
    
    public void Teilnemer(Socket socket, String name){
        
        this.name= name;
        this.socket = socket; 
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }
    
}
