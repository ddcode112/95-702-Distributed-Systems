/**
 * This program implements a UDP server.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 20, 2023
 */
// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoServerUDP{
    /**
     * Implement a UDP server.
     * @param args Array of strings from the console
     */
    public static void main(String args[]){
        // Announce the server starts running
        System.out.println("The UDP server is running.");
        // Get the port number this server to listen on from user
        // For this project, use 6789
        Scanner getPort = new Scanner(System.in);
        System.out.println("Insert the server side port number: ");
        int serverPort = getPort.nextInt();
        // Declare a Datagram (UDP style) socket
        DatagramSocket aSocket = null;
        // Prepare buffer
        byte[] buffer = new byte[1000];
        try{
            // Create a new DatagramSocket and bind it to port number from user input
            aSocket = new DatagramSocket(serverPort);
            // Create a new DatagramPacket for receiving requests
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            // An infinite loop to wait for incoming datagrams
            while(true){
                // Receive a datagram
                aSocket.receive(request);
                /*
                Create a new DatagramPacket for sending replies
                with request's data, length, address, and port number.
                 */
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                // Convert the request data to a String
                String requestString = new String(request.getData()).substring(0, request.getLength());

                // Print the request string
                System.out.println("Echoing: " + requestString);

                // Send a reply datagram back to the client
                aSocket.send(reply);

                // Quit the server when user requests halt!
                if(requestString.equals("halt!")){
                    System.out.println("UDP Server side quitting");
                    break;
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
