/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author CKC
 */
public class DataTimeProtocoll {
    
    static SimpleDateFormat time = new SimpleDateFormat("’Es ist gerade ’H’.’mm’ Uhr.’");
    static SimpleDateFormat date = new SimpleDateFormat("’Heute ist ’EEEE’, der ’dd.MM.yy");
    Socket s; // Socket in Verbindung mit dem Client
    BufferedReader vomClient; // Eingabe-Strom vom Client
    PrintWriter zumClient; // Ausgabe-Strom zum Client
    public DataTimeProtocoll (Socket s) { // Konstruktor 
        try { 
            this.s = s; 
            vomClient = new BufferedReader( 
                new InputStreamReader( 
                    s.getInputStream()));
            zumClient = new PrintWriter(s.getOutputStream(),true);
        } catch (IOException e) {
            System.out.println("IO-Error");
            e.printStackTrace();
        }
    }
    public void transact() { // Methode, die das Protokoll abwickelt 
        System.out.println("Protokoll gestartet"); 
        try { 
            zumClient.println("Geben Sie DATE oder TIME ein");
            String wunsch = vomClient.readLine(); // v. Client empfangen
            Date jetzt = new Date(); // Zeitpunkt bestimmen vom Client empfangenes Kommando ausfuehren 
            if (wunsch.equalsIgnoreCase("date")) 
                zumClient.println(date.format(jetzt)); 
            else if (wunsch.equalsIgnoreCase("time")) 
                zumClient.println(time.format(jetzt)); 
            else 
                zumClient.println(wunsch +" ist als Kommando unzulaessig!"); 
            s.close(); // Socket (und damit auch Stroeme) schliessen 
        } catch (IOException e) { 
            System.out.println("IO-Error"); 
        } 
        System.out.println("Protokoll beendet"); 
    } 
}
