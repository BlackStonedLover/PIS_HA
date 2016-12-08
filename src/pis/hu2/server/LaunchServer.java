/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author CKC
 */
public class LaunchServer   {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            int port = 2222; // Port-Nummer
            ServerSocket server = new ServerSocket(port); // Server-Socket
            System.out.println("DateTimeServer laeuft"); // Statusmeldung
            Socket s = server.accept(); // Client-Verbindung akzeptieren
            new DataTimeProtocoll(s).transact(); // Protokoll abwickeln
        } catch (ArrayIndexOutOfBoundsException ae) {
            System.out.println("Aufruf: java DateTimeServer <Port-Nr>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //launch(args);
        
    }
    
}
