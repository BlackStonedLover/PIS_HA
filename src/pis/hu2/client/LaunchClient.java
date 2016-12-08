package pis.hu2.client;

/**
 * Created by Dijivu on 08.12.2016.
 */
public class LaunchClient {

    private static  String adress ="localhost";
    private static int port =2222;

    public static void main(String[] args) {
        Client c = new Client(adress,port,"Test");
    }


}
