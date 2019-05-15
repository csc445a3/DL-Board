package DataStorage;

import java.io.File;
import java.io.IOException;

public class User {

    private FileUtils fileUtils;
    private String userName;
    private File rootDir;

    public User(String userName){

        this.userName = userName;
        rootDir = new File("users");
        rootDir.mkdir();
        fileUtils = new FileUtils(rootDir);
        try {
            if (!rootDir.exists())
                rootDir.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void put(String message, String userName, String time){
        try {
            fileUtils.put(message, userName, time);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
