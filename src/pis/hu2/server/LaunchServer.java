/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

/**
 *
 * @author Stephan Wolfgang Kusch
 */
public class LaunchServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ServerGUI ui = new ServerGUI(new Server());
        new Thread(ui).start();
    }   
}
