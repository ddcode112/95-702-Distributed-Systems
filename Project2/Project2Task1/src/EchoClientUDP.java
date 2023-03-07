/**
 * This program implements a UDP client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 20, 2023
 */

// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoClientUDP{
    /**
     * Implement a UDP client.
     * @param args Array of strings giving message contents and server hostname
     */
    public static void main(String args[]){
        // Announce the client starts running
        System.out.println("The UDP client is running.");
        // Get the server side port number from user
        // For this project, use 6789
        Scanner getDestPort = new Scanner(System.in);
        System.out.println("Insert the server side port number: ");
        int serverPort = getDestPort.nextInt();
        // Declare a Datagram (UDP style) socket
        DatagramSocket aSocket = null;
        try {
            // Collect the IP address
            InetAddress aHost = InetAddress.getByName("localhost");
            // Create the socket
            aSocket = new DatagramSocket();
            String nextLine;
            // Initialize a BufferedReader to read input from the console
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // Read lines of input
            while ((nextLine = typed.readLine()) != null) {
                // Convert the line into byte array
                byte [] m = nextLine.getBytes();
                /*
                Build the packet holding the byte message from the console, length of the message,
                destination address, and the destination port number.
                 */
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
                // Send the Datagram request on the socket
                aSocket.send(request);
                // Prepare buffer for the reply
                byte[] buffer = new byte[1000];
                // Create a Datagram for the reply
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                // Wait and receive the reply
                aSocket.receive(reply);
                String replyStr = new String(reply.getData()).substring(0, reply.getLength());
                // Show the result to the client
                System.out.println("Reply from server: " + replyStr);

                // Quit the client when user requests halt! and get response halt! by server
                if(replyStr.equals("halt!")) {
                    System.out.println("UDP Client side quitting");
                    break;
                }

            }
        // Handle socket exceptions
        }catch (SocketException e) {System.out.println("Socket Exception: " + e.getMessage());
            // Handle general IO exceptions
        }catch (IOException e){System.out.println("IO Exception: " + e.getMessage());
            // Close the socket if not null
        }finally {if(aSocket != null) aSocket.close();}
    }
}