package pis.hu2.common;

/**
 * @author Kühn, Konstantin
 * @matrikelnummer 5060992
 * @hausübung PIS Hausübung 2
 *
 */
public class Message {
    /**
     * Diese Klasse sorgt dafür, das die Nachrichten in der richtigen Art und weiße erstellt werden.
     */
    private String command="";
    private String message="";

    /**
     * Konstruktor für einkommende Nachrichten.
     * @param inputmsg
     */
    public  Message(String inputmsg){
        //System.out.println(inputmsg);
        String[] msg = inputmsg.split(":");
        this.command = msg[0];
        //System.out.println(command);
        if(command.equals("namelist")){
            
            for(int i =1; i<msg.length;i++){

                this.message = this.message + msg[i] + ":";
            }
        } else if(command.equals("message") && msg.length > 1){
            
            this.message = msg[1];
            
            for(int i = 2 ; i<msg.length;i++){
                
                this.message = this.message +":"+ msg[i];
            }
            System.out.println("Message ist : " + message);
            
        } else {
            
            for(int i = 1; i<msg.length;i++){
                
                this.message = this.message + msg[i];
            }
            System.out.println("Message ist : " + message);
        }
        //message += "\n";
        //System.out.print(message);
    }

    /**
     * Konstruktor für die Erstellung einer Nachricht
     * @param com
     * @param name
     * @param msg
     */
    public  Message(String com, String name, String msg){ 
        
        if(!msg.contains(":")){
            
            this.command = com;
            this.message = name + ":" + msg;
           // System.out.println("Message Log| Message = " + message);
        }
    }
    
    public  Message(String com,String msg){ 
        if(!msg.contains(":")){
            this.command = com;
            this.message = msg;
           // System.out.println("Message Log| Message = " + message);
        }
    }
    

    /**
     * Fügt aus einem Commando und einer Nachricht eine syntaktisch korrekte Nachricht.
     * @return String Nachricht zu verschicken
     */

    public String  getMessageAsString(){
     //   System.out.println("Message Log| getMessageAsString = " + message);
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
        cmdMsg[1] = message ;
       // System.out.println("Message Log| String Array Message = " + cmdMsg[1]);
        return cmdMsg;
    }
/**
*Gibt alle User in einem String Array zurück.
*@return Strin[] mit allen Nutzern
 */
    public String[] getAllUserNames(){
        String[] allUserNames;
        if(command.equals("namelist")){
            allUserNames = message.split(":");
        }
        else{
            return  null;
        }
        return  allUserNames;
    }
}