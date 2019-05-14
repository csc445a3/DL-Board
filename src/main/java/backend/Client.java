package backend;

import backend.MessagePacket;
import backend.RequestPacket;
import database.UserDao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.security.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
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
    static MulticastSocket ms = null;
    static final int port = 2704;
    static String id;
    static InetAddress ip = null;
<<<<<<< HEAD
    private UserDao userDao = new UserDao();
=======
    static private PrivateKey privateKey;
    static private PublicKey publicKey;
    static private ArrayList<String> messages;
>>>>>>> 4fca0267e0fbd031d446a2f880918404d91a9bd7

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
            ms = new MulticastSocket(port);

            //create an inetaddress group to join
            //this will be who we send messages to on the network
            group = InetAddress.getByName("225.0.0.0");
            ms.joinGroup(group);
            
       
            

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
            ms.send(outgoingPacket);

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
            ms.send(outgoingPacket);

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
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static byte[] recieve() throws Exception {
        try {

            byte[] buf = new byte[64000];
            DatagramPacket incomingPacket
                    = new DatagramPacket(buf, buf.length);

            ms.receive(incomingPacket);

            //decrypt message
            byte[] recievedMessage = decrypt(publicKey, incomingPacket.getData());

            return recievedMessage;

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

<<<<<<< HEAD
    /**
     * Gets userID for message based on the username
     * @param userName username is the id the user chooses
     * @return the UserID which is procedurally generated by the database.
     */
    private String selectUserID(String userName) {

        String userID = userDao.selectUserID(userName);
        return userID;

=======
    public static void recieveUpdate() {
        boolean recieving = true;
        ArrayList<byte[]> recievedMessages = new ArrayList<>();

        while (recieving) {
            try {
                ms.setSoTimeout(10000);
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
>>>>>>> 4fca0267e0fbd031d446a2f880918404d91a9bd7
    }

}
