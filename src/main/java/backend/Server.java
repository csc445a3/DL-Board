
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author asuit
 */
public class Server {

    static InetAddress group = null;
    static MulticastSocket ds = null;
    static final int port = 2704;

    public static void main(String[] args) {
        
        Setup();
        
    }

    public static void Setup() {
        try {

            //create a multicast socket
            ds = new MulticastSocket(port);

            //create an inetaddress group to join
            //this will be who we send messages to on the network
            group = InetAddress.getByName("225.0.0.0");
            ds.joinGroup(group);

            //send a message to everyone in the group
            String publishMessage = "Server Online";
            byte[] buf = publishMessage.getBytes();

            send(buf);
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void send(byte[] outputMessage) {

        try {
            DatagramPacket outgoingPacket
                    = new DatagramPacket(outputMessage, outputMessage.length, group, port);

            //send packets
            ds.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static DatagramPacket recieve(DatagramSocket ds) {
        try {

            byte[] buf = new byte[64000];
            DatagramPacket incomingPacket
                    = new DatagramPacket(buf, buf.length);

            ds.receive(incomingPacket);

            return incomingPacket;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

}
