package bgu.spl.net.impl.stomp;

import java.text.StringCharacterIterator;
import java.util.HashMap;

public class StompFrameExecuter {

    HashMap<String, String> receiveMap;
    HashMap<String, String> responseMap;
    DataBase db;
    final String version = "1.2";
    final String hostBgu = "stomp.cs.bgu.ac.il";


    enum StompCommandClient{
        CONNECT,
        SEND,
        SUBSCRIBE,
        UNSUBSCRIBE,
        DISCONNECT
    }

    enum StompCommandServer{
        CONNECTED,
        MESSAGE,
        RECEIPT,
        ERROR
    }

    StompFrameExecuter(DataBase db){
        receiveMap = new HashMap<>();
        responseMap = new HashMap<>();
        this.db = db;
    }

    private static boolean contains(String StompCommand) {

        for (StompCommandClient c : StompCommandClient.values()) {
            if (c.name().equals(StompCommand)) {
                return true;
            }
        }   
        return false;
    }

    // find the StompCommand index in the msg
    private int findIndex(String msg){
        int count =0;
        while(msg.charAt(count) != '\n'){
            count++;
        }
        return count++;
    }

    // create the receiveMap with all the headers from msg
    private void createFrameFormat(String msg){
        int StompComIndex = findIndex(msg);
        receiveMap.put("StompCommand", msg.substring(0, StompComIndex));
        String msgString = msg.substring(StompComIndex);
        String[] keyValuePairs = msgString.split("\n");
        for(String pair: keyValuePairs){
            String[] entery = pair.split(":");
            receiveMap.put(entery[0], entery[1]);
        }
    }

    // convert the map respons to string if needed
    private String convertFromMapToString(HashMap<String, String> map){
        String respoString = null;
        respoString = map.get("StompCommand") + "\n";
        for(String key : map.keySet()){
            if(!key.equals("StompCommand")){
                respoString = respoString + key + ": " + map.get(key) + "\n";
            }
        }
        return respoString;
    }

    public String execute(String msg){
        createFrameFormat(msg);
        // check if the stompCommand is valid
        if(!contains(receiveMap.get("StompCommand"))){
            String body = "The StompCommad received does not exist";
            Error(body);
            return convertFromMapToString(responseMap);
        }
        if(receiveMap.get("StompCommand").equals(StompCommandClient.CONNECT.name()))
            Connect();
        if(receiveMap.get("StompCommand").equals(StompCommandClient.SUBSCRIBE.name()))
            Subscribe();
        if(receiveMap.get("StompCommand").equals(StompCommandClient.UNSUBSCRIBE.name()))
            Subscribe();
        
        //TODO >> verify Connection, Subscribe, Send, Unsubscibe, Disconnect

        return convertFromMapToString(responseMap);
            
    }

    private void Error(String body){
        responseMap.put("StompCommand", StompCommandServer.ERROR.name());
        if(body != null)
            responseMap.put("message", body);
    }

    // check optional errors and if they don't exist add the user to DB and create CONNECTED frame
    private void Connect(){
        if(!receiveMap.containsKey("login"))
            Error("Login header user is missing.");
        
        else if(!receiveMap.containsKey("passcode"))
            Error("Passcode header user is missing.");
        
        else if(db.isTheUserConnected(receiveMap.get("login")))
            Error("User is already connected");
        
        else if(!receiveMap.containsKey("accept-version"))
            Error("Accept-version header is missing.");
        
        else if(!receiveMap.get("accept-version").equals(version))
            Error("Sent version is illegal");
        
        else if(!receiveMap.containsKey("host"))
            Error("Host header is missing.");
        
        else if(!receiveMap.get("host").equals(hostBgu))
            Error("Host is illegal");
        
        else{
            db.addConnectedUser(receiveMap.get("login"), receiveMap.get("passcode"));
            responseMap.put("StompCommand", StompCommandServer.CONNECTED.name());
            responseMap.put("version", version);
        }
        
    }

    private void Subscribe(){
        if(!receiveMap.containsKey("destination"))
            Error("Destination header is missing.");

        else if(!db.topicExist(receiveMap.get("destination")))
            Error("Topic does not exist.");
        
        else if(!receiveMap.containsKey("id"))
            Error("Id header is missing.");
    }

    private void Unsubscribe(){
    }
    
    private void Send(){
    }

    private void Disconnect(){
    }

}
