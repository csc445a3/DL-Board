package backend;

import DataStorage.User;

public class tester {

    public static void main(String[] args){

        User u = new User("jtrynisk");
        u.put("Hello", "jtrynisk");
        u.put("World", "jtrynisk");
        u.put("Testing this", "jtrynisk");

        try{

            Server s = new Server();
            s.getAllMessages("user/");

        }catch(Throwable throwable){

        }

    }

}
