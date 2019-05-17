package backend;

public class pitest {

    public static void main(String args[]) {

        while (true) {
            try {
                Client c = new Client();
                c.connect("239.0.0.193");
                MessagePacket mp = c.receive();
                System.out.println("ID: " + mp.getId().trim() + ", TimeStamp: " + mp.getTimeStamp() + ", Message: " + mp.getMessage().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
