package backend;

import DataStorage.User;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class tester {

    public static void main(String[] args){

        User u = new User("jtrynisk");
        //u.put("Hello", "jtrynisk");
        //u.put("World", "jtrynisk");
        //u.put("Testing this", "jtrynisk");

        try{

            Server s = new Server();
            File temp = new File("users");
            System.out.println(temp.getPath());
            //Set messages = s.getAllMessages(temp.getPath());
            //Iterator iter = messages.iterator();
            //while(iter.hasNext()){
            //    System.out.println(iter.next());
            //}
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
