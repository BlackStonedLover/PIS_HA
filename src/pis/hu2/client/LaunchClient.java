package pis.hu2.client;

/**
 * @author Kühn, Konstantin
 * @matrikelnummer 5060992
 * @hausübung PIS Hausübung 2
 */
public class LaunchClient {


    public static void main(String[] args) {
        ClientGUI ui = new ClientGUI(new Client());
        new Thread(ui).start();

    }


}
