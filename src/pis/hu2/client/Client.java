package pis.hu2.client;

import pis.hu2.common.Message;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by Dijivu on 08.12.2016.
 */
public class Client {

    private  String ip;
    private int port;
    private String nickname ="";

    private int maxNameLenght = 30;

    private boolean conectionWithServer = false;

    private BufferedReader fromServer;
    private PrintWriter toServer ;
    private Socket s;


    private enum commandState{
        connect,
        message,
        disconect

    }

    public  Client(String ip, int port, String nickname){
        this.ip = ip;
        this.port = port;

        if(nickname.length() < maxNameLenght && !nickname.contains(":")){
            this.nickname = nickname;
            connect();
        }

    }

    public  void connect() {
        try {
            s = new Socket(ip, port);
            fromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            toServer = new PrintWriter(s.getOutputStream(), true);

            Message msg = new Message("connect", nickname);
            toServer.write(msg.getMessageAsString());
            Message result = new Message(fromServer.readLine());
            if (result.getMessageAsString().equals("connect:ok")) ;
            {
                //Verbindung Aufgebaut
                System.out.println("Verbindung hergestellt");
            }
        } catch (ArrayIndexOutOfBoundsException ae) {

            System.out.println("Aufruf: ");
            System.out.println("java Client <HostName> <PortNr> ");
        } catch (UnknownHostException ue) {
            System.out.println("Kein DNS-Eintrag fuer " + ip);
        } catch (IOException e) {
            //System.out.println(e);
            System.out.println("IO-Error");
        }
    }

    public  void coneccted(){
        while(conectionWithServer){

        }
    }



}
