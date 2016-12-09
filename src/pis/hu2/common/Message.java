package pis.hu2.common;

/**
 * Created by Dijivu on 08.12.2016.
 */
public class Message {

   private String command="";
    private String message="";

    public  Message(String inputmsg){
        System.out.println(inputmsg);
        String[] msg = inputmsg.split(":");
        this.command = msg[0];
        System.out.println(command);
        for(int i =1; i<msg.length;i++){
            this.message += msg[i];
        }
        message += "\n";
        System.out.print(message);
    }

    public  Message(String com,String msg){
        if(!msg.contains(":")){
            this.command = com;
            this.message = msg+"\n";
        }
    }

    /**
     * Fügt aus einem Commando und einer Nachricht eine syntaktisch korrekte Nachricht.
     * @return String Nachricht zu verschicken
     */
    public String  getMessageAsString(){
        return command +":" + message;
    }

    /**
     * Gibt ein String Array zurück.
     * Das erste Array ist das Komando.
     * Das zweite Array ist die Nachricht.
     * @return String[] Command& MessageString
     */
    public String[]  getMessageAsStringArray(){
        String[] cmdMsg = new String[2];
        cmdMsg[0] = command;
        cmdMsg[1] =message;
        return cmdMsg;
    }


}
