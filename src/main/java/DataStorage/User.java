package DataStorage;

import java.io.File;
import java.io.IOException;

public class User {

    private FileUtils fileUtils;
    private String userName;
    private File rootDir;
    private int messageID;

    public User(String userName){

        this.userName = userName;
        rootDir = new File("users");
        rootDir.mkdir();
        fileUtils = new FileUtils(rootDir);
        messageID = 0;
        try {
            if (!rootDir.exists())
                rootDir.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void put(String message, String userName){
        try {
            messageID++;
            fileUtils.put(message, userName, Integer.toString(messageID));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
