package backend;

public class ServerMain {

    public static void main(String[] args){

        try {
            Server s = new Server();
            while(true){
                System.out.println("Running server");
                s.recieve();
                
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }

}
