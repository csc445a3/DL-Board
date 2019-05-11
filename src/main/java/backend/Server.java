package  backend;

import backend.MessagePacket;
import backend.RequestPacket;
import backend.UpdatePacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 *
 * @author asuit
 */
public class Server {

    static InetAddress group = null;
    static MulticastSocket ds = null;
    static final int port = 2704;

    
    public Server() {
        
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
    
    public static void sendUpdate(byte[] outputMessage,InetAddress ip) {
        //How do we send multiple messages?
       //ip is to send to a specific client
       //get it from the requestpacket the client sends to the server
        
        try {

            UpdatePacket updMsg = new UpdatePacket(outputMessage);

            DatagramPacket outgoingPacket
                    = new DatagramPacket(updMsg.getSendMessage(), updMsg.getSendMsgLength(), ip, port);

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
            
            //send to processing
            processPacket(incomingPacket);
            
            return incomingPacket;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    public static void processPacket(DatagramPacket p){
        byte[] incomingBytes;
        incomingBytes = Arrays.copyOfRange(p.getData(), 0, p.getLength());
        
        //take the first two bytes of the incoming packet, which should be the opcode
        byte[] opcode = Arrays.copyOfRange(incomingBytes, 0, 1);
        
        if(opcode[0] == 0 && opcode[1] == 1){
            //this is the opcode for 
            //a requestpacket from client to server
            //
            //the rest of the message should be an ip
            
            try{
                byte[] clientIP = Arrays.copyOfRange(incomingBytes, 1, incomingBytes.length);

                InetAddress ip = InetAddress.getByAddress(clientIP);
            
                //after finding the ip create a RequestPacket so we can get info from it
                RequestPacket rp = new RequestPacket(ip);
                
                //What now?
                //Send requestPacket to a database method
                //That then calls the sendPacket method?
                
                
            }   catch (IOException ex) {
                System.out.println("Error in processing requestpacket");
                System.out.println(ex.getMessage());
            }
            

        }else if(opcode[0] == 0 && opcode[1] == 2){
            //this is the opcode for
            //a messagepacket from client to other clients, processed by the server
            
            try{
                //how many bytes does the IP Store as?
                byte[] clientIP = Arrays.copyOfRange(incomingBytes, 1, 2);

                InetAddress ip = InetAddress.getByAddress(clientIP);
                
                //ID is 12 bytes long
                byte[] clientID = Arrays.copyOfRange(incomingBytes,2, 14);
                String id = new String(clientID);
                
                byte[] clientMSG = Arrays.copyOfRange(incomingBytes, 14, incomingBytes.length - 23);
                
                
                //Time is 23 Bytes long
                byte[] clientTime = Arrays.copyOfRange(incomingBytes,incomingBytes.length - 23, incomingBytes.length);
                String stringTime = new String(clientTime);
                LocalDateTime time = LocalDateTime.parse(stringTime);
                
                
                //Create the message packet
                MessagePacket mp = new MessagePacket(id,clientMSG, time);
                
                
                //What now?
                //Send messagePacket to a database method?
                
                
            }   catch (IOException ex) {
                System.out.println("Error in processing messagepacket");
                System.out.println(ex.getMessage());
            }
            
            
            
        }
        
        //Do nothing if opcode is something else
        //probably should never be anything else
        
        
    }

}
