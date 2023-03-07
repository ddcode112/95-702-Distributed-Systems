/**
 * This program implements a UDP server.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 20, 2023
 */
// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class AddingServerUDP{
    // sum to record the total added values
    static int sum = 0;
    /**
     * Implement a UDP server.
     * @param args Array of strings from the console
     */
    public static void main(String args[]){
        // Announce the server starts running
        System.out.println("Server started");
        // Get the port number this server to listen on from user
        int serverPort = 6789;
        // Declare a Datagram (UDP style) socket
        DatagramSocket aSocket = null;
        // Prepare buffer for integer
        byte[] requestBuffer = new byte[4];
        try{
            // Create a new DatagramSocket and bind it to port number from user input
            aSocket = new DatagramSocket(serverPort);
            // Create a new DatagramPacket for receiving requests
            DatagramPacket request = new DatagramPacket(requestBuffer, requestBuffer.length);
            // An infinite loop to wait for incoming datagrams
            while(true){
                // Receive a datagram
                aSocket.receive(request);
                // Convert the request byte array into integer
                ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.put(request.getData());
                buffer.rewind();
                int numAdded = buffer.getInt();
                // Call add method to calculate
                add(numAdded);
                // Convert sum to byte array
                byte[] replySum = ByteBuffer.allocate(4).putInt(sum).array();
                /*
                Create a new DatagramPacket for sending replies
                with byte array of sum, array length, request address, and request port number.
                */
                DatagramPacket reply = new DatagramPacket(replySum,
                            replySum.length, request.getAddress(), request.getPort());
                // Send a reply datagram back to the client
                aSocket.send(reply);
                // Print reply action
                System.out.println("Returning sum of " + sum + " to client");
                System.out.println();

            }
            // Handle socket exceptions
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
            // Handle general IO exceptions
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
            // Close the socket if not null
        }finally {if(aSocket != null) aSocket.close();}
    }

    /**
     * Add integer i to sum
     * @param i integer requested from client
     * @return current sum
     */
    public static int add (int i) {
        // Print current adding action
        System.out.println("Adding " + i + " to " + sum);
        // Add i to sum
        sum += i;
        return sum;
    }
}
