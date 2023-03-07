/**
 * This program implements a UDP client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 21, 2023
 */

// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class RemoteVariableClientUDP{
    // Declare a Datagram (UDP style) socket
    static DatagramSocket aSocket = null;
    // Destination server port number
    static int serverPort;
    // Host name
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
        Scanner readInput = new Scanner(System.in);
        System.out.println("Please enter server port: ");
        serverPort = readInput.nextInt();
        System.out.println();

        try {
            // Create a Datagram (UDP style) socket
            aSocket = new DatagramSocket();
            // Initialize choice
            int choice = 0;
            // Collect the IP address
            aHost = InetAddress.getByName("localhost");
            while (true){
                // Display menu
                System.out.println("1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client");
                if (readInput.hasNextInt()) {
                    // Get choice
                    choice = readInput.nextInt();
                    // num variable to store int for add or subtract choice
                    int num;
                    // id variable to store client's ID
                    int id;
                    // Initialize request string
                    String requestStr = "";

                    switch (choice) {
                        case 1: // If user selects to add
                            System.out.println("Enter value to add: ");
                            // Get num to add
                            num = readInput.nextInt();
                            System.out.println("Enter your ID:");
                            // Get id
                            id = readInput.nextInt();
                            // Concatenate request string
                            requestStr = id + "," + choice + "," + num;
                            break;
                        case 2: // If user selects to subtract
                            System.out.println("Enter value to subtract: ");
                            // Get num to subtract
                            num = readInput.nextInt();
                            System.out.println("Enter your ID:");
                            // Get id
                            id = readInput.nextInt();
                            // Concatenate request string
                            requestStr = id + "," + choice + "," + num;
                            break;
                        case 3: // If user selects to get
                            System.out.println("Enter your ID:");
                            // Get id
                            id = readInput.nextInt();
                            // Concatenate request string
                            requestStr = id + "," + choice;
                            break;
                        case 4: // If user selects to quit
                            System.out.println("Client side quitting. The remote variable server is still running.");
                            break;
                    }
                    if (choice == 4) break; // Break the loop if clients requests to quit
                    // Call parseRequest method to communicate with server and get the result
                    int result = parseRequest(requestStr);
                    // Print result to console
                    System.out.println("The result is " + result + ".");
                    System.out.println();
                }

            }
            // Handle unknown host exceptions
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (SocketException e) { // Handle socket exceptions
            throw new RuntimeException(e);
        } finally {if(aSocket != null) aSocket.close();} // Close the socket if not null

    }

    public static int parseRequest (String requestStr) {
        // int result to record the reply sum
        int result;
        // Convert requestStr into byte array
        byte[] m = requestStr.getBytes();
        try {
            /*
            Build the packet holding the byte message from the console, length of the message,
            destination address, and the destination port number.
            */
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
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
            result = buffer.getInt();
        } catch (IOException e) { // Handle general IO exceptions
            throw new RuntimeException(e);
        }

        return result;
    }
}