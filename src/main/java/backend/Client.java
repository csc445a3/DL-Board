
import backend.MessagePacket;
import backend.RequestPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

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
    static private PrivateKey privateKey;
    static private PublicKey publicKey;
    static private ArrayList<String> messages;

    public Client() throws NoSuchAlgorithmException {
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

            //setup encryption
            //using RSA
            KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
            //key size of 1024
            kg.initialize(1024);
            KeyPair keyPair = kg.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();

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

    public static void send(byte[] outputMessage) throws Exception {

        try {
            //encrypt the message using RSA
            byte[] outMsg = encrypt(privateKey, outputMessage);

            DatagramPacket outgoingPacket
                    = new DatagramPacket(outputMessage, outputMessage.length, group, port);

            //send packets
            ds.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void sendMessage(byte[] outputMessage) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));

            MessagePacket msg = new MessagePacket(id, outputMessage, now);

            //Encrypt this message using RSA
            byte[] sendMsg = encrypt(privateKey, msg.getSendMessage());

            //send formatted message (SendMessage)
            DatagramPacket outgoingPacket
                    = new DatagramPacket(sendMsg, sendMsg.length, group, port);

            //send packets
            ds.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void sendRequest() throws Exception {

        try {

            RequestPacket msg = new RequestPacket(ip);

            //Encrypt this request using RSA
            byte[] sendMsg = encrypt(privateKey, msg.getSendMessage());

            DatagramPacket outgoingPacket
                    = new DatagramPacket(msg.getSendMessage(), msg.getSendMsgLength(), group, port);

            //send packets
            ds.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static byte[] recieve() throws Exception {
        try {

            byte[] buf = new byte[64000];
            DatagramPacket incomingPacket
                    = new DatagramPacket(buf, buf.length);

            ds.receive(incomingPacket);

            //decrypt message
            byte[] recievedMessage = decrypt(publicKey, incomingPacket.getData());

            return recievedMessage;

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static void recieveUpdate() {
        boolean recieving = true;
        ArrayList<byte[]> recievedMessages = new ArrayList<>();

        while (recieving) {
            try {
                ds.setSoTimeout(10000);
                //add messages to an arraylist
                recievedMessages.add(recieve());

            } catch (SocketException e) {
                //havent recieved any new updates
                recieving = false;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }

        for (byte[] b : recievedMessages) {
            String rMsgString = new String(b);
            messages.add(rMsgString);
        }

    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static byte[] encrypt(PrivateKey privateKey, byte[] message) throws Exception {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, privateKey);

        return c.doFinal(message);
    }

    public static byte[] decrypt(PublicKey publicKey, byte[] encrypted) throws Exception {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, publicKey);

        return c.doFinal(encrypted);
    }

}
