package bgu.spl.net.impl.stomp;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {

    HashMap <String, String> UsersConnected;
    HashMap <String, ArrayList<String>> topicName;
    
    public DataBase(){
        UsersConnected = new HashMap<>();
        topicName = new HashMap<>();
    }

    public boolean isTheUserConnected(String user){
        return UsersConnected.containsKey(user);
    }

    public boolean topicExist(String topic){
        return topicName.containsKey(topic);
    }

    public boolean userSubscribeTopic(String topic, String user){
        if(topicExist(topic)){
            ArrayList<String> users = topicName.get(topic);
            return users.contains(user);
        }
        return false;
    }

    public void addTopic(String topic, ArrayList<String> users){
        topicName.put(topic, users);
    }

    public void addConnectedUser(String user, String password){
        UsersConnected.put(user, password);
    }

    public void disconnectUser(String user){
        UsersConnected.remove(user);
    }
    
}
