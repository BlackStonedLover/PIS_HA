package pis.hu2.common;

/**
 * Created by Dijivu on 08.12.2016.
 */
public class Message {

   private String command;
    private String message;
    public  Message(String command, String message){
        if(command != ""){
            this.command = command;
        }
        this.message = message;

    }


    public String  getfinalMessage(){
        return command +":" + message+"/n";
    }


}
