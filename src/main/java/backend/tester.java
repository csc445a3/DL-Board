package backend;

import java.util.Scanner;

public class tester {

    public static void main(String args[]) {

        Client c = null;

            try {
                c.connect("239.0.0.193");
             
                
                
                Thread sender = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                    try {
                        Scanner sc = new Scanner(System.in);
                System.out.print("Please enter and id: ");
                String id = sc.nextLine();
                System.out.print("Please enter a message: ");
                String message = sc.nextLine();
                c.send(id, message);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    }
                }
            });

            Thread listener = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                    try {
                        MessagePacket mp = c.receive();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    }
                }
            });
            sender.start();
            listener.start();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


