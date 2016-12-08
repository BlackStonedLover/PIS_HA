/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import pis.hu2.common.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author CKC
 */
public class DataTimeProtocoll {

    //    static SimpleDateFormat time = new SimpleDateFormat("’Es ist gerade ’H’.’mm’ Uhr.’");
    //  static SimpleDateFormat date = new SimpleDateFormat("’Heute ist ’EEEE’, der ’dd.MM.yy");
    Socket s; // Socket in Verbindung mit dem Client
    BufferedReader vomClient; // Eingabe-Strom vom Client
    PrintWriter zumClient; // Ausgabe-Strom zum Client

    public DataTimeProtocoll(Socket s) { // Konstruktor
        try {
            this.s = s;
            vomClient = new BufferedReader(
                    new InputStreamReader(
                            s.getInputStream()));
            zumClient = new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("IO-Error");
            e.printStackTrace();
        }
    }

    public void transact() { // Methode, die das Protokoll abwickelt
        System.out.println("Protokoll gestartet");
        while (!s.isClosed()) {
            try {

                System.out.println("Is running");
                //String wunsch = vomClient.readLine(); // v. Client empfangen
                Message inMsg = new Message(vomClient.readLine());
                String[] msgArray = inMsg.getMessageAsStringArray();
                String command = msgArray[0];
                String message = msgArray[1];
                System.out.println(command);
                if (command.equals("connect")) {
                    Message msg = new Message("connect", "ok");
                    zumClient.println(msg.getMessageAsString());
                    System.out.println("Ok");
                } else if (command.equals("message")) {
                    Message rsp = new Message("response", "recieved " + message.subSequence(0,message.length()-1));
                    zumClient.println(rsp.getMessageAsString());
                    System.out.println(rsp.getMessageAsString());
                } else if (command.equals("disconect")) {
                    s.close();
                }


                /*else{
                    zumClient.println(wunsch +" ist als Kommando unzulaessig!");
                    s.close(); // Socket (und damit auch Stroeme) schliessen
                }*/

            }


            // s.close(); // Socket (und damit auch Stroeme) schliessen

            catch (IOException e) {
                System.out.println("IO-Error");
            }
        }
        System.out.println("Protokoll beendet");
    }
}
