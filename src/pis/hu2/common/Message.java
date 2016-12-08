package pis.hu2.common;

/**
 * Created by Dijivu on 08.12.2016.
 */
public class Message {

   private String command;
    private String message;
    public  Message(String inputmsg){

        String[] msg = inputmsg.split(":");

        this.command = msg[0];

        for(int i =1; i<msg.length;i++){
            this.message = this.message + msg[i];

        }

    }

    public  Message(String com,String msg){
        if(!msg.contains(":")){
            this.command = com;
            this.message = msg;
        }

    }


    public String  getMessageAsString(){

        return command +":" + message+"/n";
    }


    public String[]  getMessageAsStringArray(){

        String[] cmdMsg = new String[2];
        cmdMsg[0] = command;
        cmdMsg[1] =message;
        return cmdMsg;
    }


}
