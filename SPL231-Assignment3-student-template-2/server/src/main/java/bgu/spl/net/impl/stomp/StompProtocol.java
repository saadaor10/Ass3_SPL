package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessagingProtocol;

public class StompProtocol implements MessagingProtocol<String>{
    
    private boolean shouldTerminate;
    private DataBase db;
    public StompProtocol(DataBase db){
        this.shouldTerminate = false;
        this.db = db;
    }
    final char nullVal = '\u0000';
    final String nullV = "\u0000";
    /**
     * process the given message 
     * @param msg the received message
     * @return the response to send or null if no response is expected by the client
     */
    public String process(String msg){
        
        shouldTerminate = msg.equals(nullV);
        return CreateStomp(msg);
    }

    public String CreateStomp(String msg){
        StompFrameExecuter stompEx = new StompFrameExecuter(db);
        return stompEx.execute(msg);
    }
 
    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        return shouldTerminate;
    }
    
}
