/**
 * This program implements a UDP server.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 22, 2023
 */
// Import the necessary packages for UDP
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.TreeMap;

public class RemoteVariableServerUDP{
    // TreeMap to store the sum for each id
    static TreeMap<Integer, Integer> idSumMap = null;
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
        byte[] requestBuffer = new byte[1000];
        try{
            // Create a new DatagramSocket and bind it to port number from user input
            aSocket = new DatagramSocket(serverPort);
            // Initialize the TreeMap
            idSumMap = new TreeMap<>();
            // Create a new DatagramPacket for receiving requests
            DatagramPacket request = new DatagramPacket(requestBuffer, requestBuffer.length);
            // An infinite loop to wait for incoming datagrams
            while(true){
                // Receive a datagram
                aSocket.receive(request);
                // Convert the request byte array into string
                String requestStr = new String(request.getData()).substring(0, request.getLength());
                // Split the requestStr to array [id, choice, (num)]
                String[] requestArr = requestStr.split(",");
                // Get id
                Integer id = Integer.parseInt(requestArr[0]);
                // Add id to map if the id hasn't requested before
                if (!idSumMap.containsKey(id)) {
                    idSumMap.put(id, 0);
                }
                // Get choice
                int choice = Integer.parseInt(requestArr[1]);
                int num;
                if (choice == 1) { // if the choice is adding
                    // Get the num to be added
                    num = Integer.parseInt(requestArr[2]);
                    // Call add method
                    add(id, num);
                } else if (choice == 2) { // if the choice is subtracting
                    // Get the num to be subtracted
                    num = Integer.parseInt(requestArr[2]);
                    // Call subtract method
                    subtract(id, num);
                } else { // Print getting action
                    System.out.println("ID: " + id + " - getting sum");
                }

                // Convert sum to byte array
                byte[] replySum = ByteBuffer.allocate(4).putInt(idSumMap.get(id)).array();
                /*
                Create a new DatagramPacket for sending replies
                with byte array of sum, array length, request address, and request port number.
                */
                DatagramPacket reply = new DatagramPacket(replySum,
                            replySum.length, request.getAddress(), request.getPort());
                // Send a reply datagram back to the client
                aSocket.send(reply);
                // Print reply action
                System.out.println("Returning sum of " + idSumMap.get(id) + " to client");
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
     * Add num to sum for the id
     * @param id client ID
     * @param num int to be added
     * @return current sum for the id
     */
    public static int add (int id, int num) {
        // Print current adding action
        System.out.println("ID: " + id + " - adding " + num + " to " + idSumMap.get(id));
        // Add num to sum
        idSumMap.put(id, idSumMap.get(id) + num);
        return idSumMap.get(id);
    }

    /**
     * Subtract num to sum for the id
     * @param id client ID
     * @param num int to be subtracted
     * @return current sum for the id
     */
    public static int subtract (int id, int num) {
        // Print current subtracting action
        System.out.println("ID: " + id + " - subtracting " + num + " to " + idSumMap.get(id));
        // Subtract num to sum
        idSumMap.put(id, idSumMap.get(id) - num);
        return idSumMap.get(id);
    }
}
