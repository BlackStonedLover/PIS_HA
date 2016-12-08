package pis.hu2.client;

/**
 * Created by Dijivu on 08.12.2016.
 */
public class LaunchClient {

    private static  String adress;
    private static int port;

    public static void main(String[] args) {
        Client c = new Client(adress,port,"Test");
    }


}
