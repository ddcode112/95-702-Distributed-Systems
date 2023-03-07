/**
 * This program acts as a malicious player between server and client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 20, 2023
 */
// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.util.Scanner;
public class EavesdropperUDP {
    /**
     * Implement an Eavesdropper.
     * @param args Array of strings from the console
     */
    public static void main(String args[]){
        // Announce the Eavesdropper starts running
        System.out.println("The Eavesdropper is running.");
        // Get the port number this server to listen on from user
        // For this project, use 6789
        Scanner getPort = new Scanner(System.in);
        System.out.println("Enter the port number to listen on: ");
        int listenPort = getPort.nextInt();
        // Get the port number to masquerade as from user
        // For this project, use 6798
        System.out.println("Enter the port number of the server to masquerade as: ");
        int masqueradPort = getPort.nextInt();
        // Declare a Datagram (UDP style) socket between client
        DatagramSocket aSocket = null;
        // Declare a Datagram (UDP style) socket between server
        DatagramSocket bSocket = null;
        // Prepare buffer
        byte[] buffer = new byte[1000];
        try{
            // Collect the IP address
            InetAddress aHost = InetAddress.getByName("localhost");
            // Create a new DatagramSocket and bind it to port number from user input
            aSocket = new DatagramSocket(listenPort);
            // Create the socket
            bSocket = new DatagramSocket();
            // Create a new DatagramPacket for receiving requests from client
            DatagramPacket trueRequest = new DatagramPacket(buffer, buffer.length);
            // An infinite loop to wait for incoming datagrams
            while(true){
                // Receive a datagram from client
                aSocket.receive(trueRequest);

                // Convert the request data to a String
                String requestString = new String(trueRequest.getData()).substring(0, trueRequest.getLength());

                // Print the actual request string
                System.out.println("Message from Client: " + requestString);
                // Check if the message is general or requesting to quit
                if (!requestString.equals("halt!")) {
                    // Add ! to the actual message
                    byte[] m = (requestString + "!").getBytes();
                    String fakeRequestString = new String(m);
                    // Print the fake message to send to server
                    System.out.println("Send fake message to server: " + fakeRequestString);
                    /*
                    Build the packet holding the byte message from the console, length of the message,
                    destination address, and the destination port number.
                     */
                    DatagramPacket fakeRequest = new DatagramPacket(m, m.length, aHost, masqueradPort);
                    // Send the Datagram request on the socket to server
                    bSocket.send(fakeRequest);
                } else {
                    /*
                    Build the packet holding the byte message from the console, length of the message,
                    destination address, and the destination port number.
                     */
                    DatagramPacket request = new DatagramPacket(trueRequest.getData(), trueRequest.getLength(),
                                                                aHost, masqueradPort);
                    System.out.println("Client requests quitting");
                    // Send the Datagram request on the socket to server
                    bSocket.send(request);
                }
                // Create a Datagram for the reply from server
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                // Wait and receive the reply
                bSocket.receive(reply);
                String replyStr = new String(reply.getData()).substring(0, reply.getLength());
                // Show the result from server
                System.out.println("Reply from server: " + replyStr);
                // Check if sever replies with halt!
                if (!requestString.equals("halt!")) {
                    // Remove ! from the reply
                    byte[] mock_m = replyStr.substring(0, replyStr.length() - 1).getBytes();
                    String fakeReplyString = new String(mock_m);
                    System.out.println("Reply the original message: " + fakeReplyString);
                    /*
                    Build the packet holding the byte message from the console, length of the message,
                    destination address, and the destination port number.
                     */
                    DatagramPacket fakeReply = new DatagramPacket(mock_m, mock_m.length,
                                                    trueRequest.getAddress(), trueRequest.getPort());
                    // Send a reply datagram back to the client
                    aSocket.send(fakeReply);
                } else {
                    /*
                    Build the packet holding the byte message to halt from the console, length of the message,
                    destination address, and the destination port number.
                     */
                    DatagramPacket haltReply = new DatagramPacket(reply.getData(), reply.getLength(),
                                                    trueRequest.getAddress(), trueRequest.getPort());
                    System.out.println("Server quits");
                    // Send a reply datagram back to the client
                    aSocket.send(haltReply);
                }

            }
            // Handle socket exceptions
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
            // Handle general IO exceptions
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
            // Close the socket if not null
        }finally {if(aSocket != null) aSocket.close();}
    }
}