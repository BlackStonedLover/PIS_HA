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

    public void connect() {
        keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            s = new Socket(ip, port);
            fromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            toServer = new PrintWriter(s.getOutputStream(), true);

            Message msg = new Message("connect", nickname);
            sendMessage(msg);
            listen();

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

    public void listen() {
        if (s != null) {
            while (!s.isClosed()) {
                try {

                    if (keyboard.read() != '\n') {
                        Message outputMsg = new Message("message", keyboard.readLine());
                        sendMessage(outputMsg);
                    }
                    if (fromServer.readLine() != null) {
                        Message msgServer = new Message(fromServer.readLine());
                        String[] msgArray = msgServer.getMessageAsStringArray();
                        String command = msgArray[0];
                        String message = msgArray[1];
                        if (!confConnection && command.equals("connect")) {

                            if (message.equals("ok")) ;
                            {
                                confConnection = true;
                            }
                        } else if (command.equals("disconect")) {
                            //Disconect
                        } else {
                            System.out.println(message);
                        }
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
        }
    }

    public void sendMessage(Message msgToSend) {
        System.out.println(msgToSend.getMessageAsString());
        toServer.println(msgToSend.getMessageAsString());
    }

}
