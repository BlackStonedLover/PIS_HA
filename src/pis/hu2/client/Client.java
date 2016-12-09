package pis.hu2.client;

import pis.hu2.common.Message;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by Dijivu on 08.12.2016.
 */
public class Client {

    private String ip;
    private int port;
    private String nickname = "";

    private int maxNameLenght = 30;

    private boolean confConnection = false;
    BufferedReader keyboard;

    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Socket s;


    private enum commandState {
        connect,
        message,
        disconect

    }

    private commandState cS = commandState.connect;

    public Client(String ip, int port, String nickname) {
        this.ip = ip;
        this.port = port;

        if (nickname.length() < maxNameLenght && !nickname.contains(":")) {
            this.nickname = nickname;
            connect();
        }

    }

    /**
     * Methode connect:
     * Versucht sich mit dem Server zu verbunden und
     */
    public void connect() {
        keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            s = new Socket(ip, port);
            fromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            toServer = new PrintWriter(s.getOutputStream(), true);

            Message msg = new Message("connect", nickname);
            sendMessage(msg);


        } catch (ArrayIndexOutOfBoundsException ae) {
            System.out.println("Aufruf: ");
            System.out.println("java Client <HostName> <PortNr> ");
        } catch (UnknownHostException ue) {
            System.out.println("Kein DNS-Eintrag fuer " + ip);
        } catch (IOException e) {
            //System.out.println(e);
            System.out.println("IO-Error");
        }
        listen();
    }

    /**
     * Methode listen:
     * Versucht Nachrichten vom Server zu lesen. Wenn eine Zeile eingegeben wird, sende eine Nachricht.
     * Ansonsten lese vom Server. Überprüfen die Commands die der Server liefert.
     */
    public void listen() {
        if (s != null) {
            while (!s.isClosed()) {
                try {

                    if (keyboard.readLine().contains("\n")) {
                        Message outputMsg = new Message("message", keyboard.readLine());
                        sendMessage(outputMsg);
                    }else

                    if(fromServer.readLine().contains("connect")){
                        System.out.println("Conected");
                    }
                    else{
                        System.out.println("Nothing Happened");
                    }
                    /*
                        Message msgServer = new Message(fromServer.readLine());
                        String[] msgArray = msgServer.getMessageAsStringArray();
                        String command = msgArray[0];
                        String message = msgArray[1];
                        System.out.println("Got an Command: " +command);
                        if (!confConnection && command.equals("connect")) {

                            if (message.equals("ok")) ;
                            {
                                confConnection = true;
                            }

                        } else if (command.equals("disconect")) {
                            s.close();
                        } else {
                            System.out.println(message);
                        }

*/

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
        }
    }

    public void sendMessage(Message msgToSend) {
        //System.out.println(msgToSend.getMessageAsString());
        String msgOut = msgToSend.getMessageAsString();
        toServer.println(msgOut);
    }

}
