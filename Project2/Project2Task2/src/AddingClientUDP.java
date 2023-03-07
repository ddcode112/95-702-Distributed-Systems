/**
 * This program implements a UDP client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 20, 2023
 */

// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class AddingClientUDP{
    static int serverPort;
    static InetAddress aHost;
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
        System.out.println("Please enter server port: ");
        serverPort = getDestPort.nextInt();
        System.out.println();
        try {
            String nextLine;
            // Collect the IP address
            aHost = InetAddress.getByName("localhost");
            // Initialize a BufferedReader to read input from the console
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // Read lines of input
            while ((nextLine = typed.readLine()) != null) {
                // if the input matches integer format
                if (nextLine.matches("^[+-]*[0-9]+$")) {
                    // Convert the input into an integer
                    int num = Integer.parseInt(nextLine);
                    // Call add method to communicate with server and get the current sum
                    int replySum = add(num);
                    // Print result to console
                    System.out.println("The server returned " + replySum + ".");
                } else if (nextLine.equals("halt!")) { // if client requests to quit
                    System.out.println("Client side quitting.");
                    break;
                } else { // input other than integer or halt, continue to loop
                    System.out.println("Please enter an integer.");
                    continue;
                }
            }
            // Handle unknown host exceptions
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
            // Handle general IO exceptions
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static int add (int i) {
        // int sum to record the reply sum
        int sum;
        // Convert i into byte array
        byte[] m = ByteBuffer.allocate(4).putInt(i).array();
        // Declare a Datagram (UDP style) socket
        DatagramSocket aSocket = null;
        try {
            // Create a Datagram (UDP style) socket
            aSocket = new DatagramSocket();
            /*
            Build the packet holding the byte message from the console, length of the message,
            destination address, and the destination port number.
            */
            DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
            // Send the Datagram request on the socket
            aSocket.send(request);
            // Prepare buffer for the reply
            byte[] replyBuffer = new byte[4];
            // Create a Datagram for the reply
            DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
            // Wait and receive the reply
            aSocket.receive(reply);
            // Convert reply into integer
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.put(reply.getData());
            buffer.rewind();
            sum = buffer.getInt();
            // Handle socket exceptions
        } catch (SocketException e) {
            throw new RuntimeException(e);
            // Handle general IO exceptions
        } catch (IOException e) {
            throw new RuntimeException(e);
            // Close the socket if not null
        } finally {if(aSocket != null) aSocket.close();}

        return sum;
    }
}