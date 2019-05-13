
import backend.MessagePacket;
import backend.RequestPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asuit
 */
public class Client {

    static InetAddress group = null;
    static MulticastSocket ds = null;
    static final int port = 2704;
    static String id;
    static InetAddress ip = null;

    public Client() {
        try {
            //assign a random name
            String format = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            while (sb.length() < 12) { // length of the random string
                int index = (int) (r.nextFloat() * format.length());
                sb.append(format.charAt(index));
            }

            //set ip and id
            ip = InetAddress.getLocalHost();
            id = sb.toString();

            //start connection
            Setup();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void Setup() {
        try {

            //create a multicast socket
            ds = new MulticastSocket(port);

            //create an inetaddress group to join
            //this will be who we send messages to on the network
            group = InetAddress.getByName("225.0.0.0");
            ds.joinGroup(group);

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

    public static void sendMessage(byte[] outputMessage) {

        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));

            MessagePacket msg = new MessagePacket(id, outputMessage, now);

            //send formatted message (SendMessage)
            DatagramPacket outgoingPacket
                    = new DatagramPacket(msg.getSendMessage(), msg.getSendMsgLength(), group, port);

            //send packets
            ds.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void sendRequest() {

        try {

            RequestPacket msg = new RequestPacket(ip);

            DatagramPacket outgoingPacket
                    = new DatagramPacket(msg.getSendMessage(), msg.getSendMsgLength(), group, port);

            //send packets
            ds.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static DatagramPacket recieve() {
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
