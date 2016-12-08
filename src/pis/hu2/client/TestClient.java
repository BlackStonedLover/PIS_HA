package pis.hu2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Dijivu on 08.12.2016.
 */
 public class TestClient {



    public static void main(String[] args) {
        String host ="213.73.99.169"; // Rechner-Name bzw. Adresse
        int port = 2222; // Port Nummer
        Socket s = null;
        try{

            s = new Socket( host,port);
            BufferedReader toServer = new BufferedReader( new InputStreamReader(s.getInputStream()));
            PrintWriter fromServer = new PrintWriter(s.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));


            System.out.println("Server " + host + ":" + port + " sagt:");
            String text = toServer.readLine();
            System.out.println(text);
            text = keyboard.readLine();
            fromServer.println(text);
            text = toServer.readLine();
            System.out.println(text);
            s.close();
        }
        catch (ArrayIndexOutOfBoundsException ae){

                System.out.println("Aufruf: ");
            System.out.println("java Client <HostName> <PortNr> ");
        }
        catch (UnknownHostException ue){
            System.out.println("Kein DNS-Eintrag fuer " + host);
        }
        catch (IOException e){
            //System.out.println(e);
        System.out.println("IO-Error");
        }
    }


    public  void connect(){

    }

}
