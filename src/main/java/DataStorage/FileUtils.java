package DataStorage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    private File root;

    public FileUtils(File root){
        this.root = root;
    }

    public boolean contains(String userName){

        File dir = new File(root.getPath());
        File[] listing = dir.listFiles();
        if (listing != null){
            for (File child : listing){
                if (child.getName() == userName)
                    return true;
            }
        }
        return false;
    }

    public void put(String message, String userName, String messageID) throws IOException {

        if (contains(userName)){
            File newFile = new File(root.getPath() + '/' + userName + '/' + messageID);
            newFile.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile.getAbsolutePath()));
            bos.write(message.getBytes());
        }
        else{
            File userDir = new File(root.getPath() + "/" + userName);
            userDir.mkdirs();
            File newFile = new File(userDir.getPath() + "/" + messageID + ".txt");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile));
            bos.write(message.getBytes());
            bos.close();
        }

    }


}
