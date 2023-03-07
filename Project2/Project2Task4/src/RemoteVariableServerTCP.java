/**
 * This program implements a TCP server.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 22, 2023
 */

// Import the necessary packages for TCP
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.TreeMap;

public class RemoteVariableServerTCP {
    // TreeMap to store the sum for each id
    static TreeMap<Integer, Integer> idSumMap = null;
    /**
     * Implement a TCP server.
     * @param args Array of strings from the console
     */
    public static void main(String args[]) {
        // Announce the server starts running
        System.out.println("Server started");
        // Port number this server to listen on
        int serverPort = 6789;
        // Initialize the TreeMap storing <id, sum>
        idSumMap = new TreeMap<>();
        // Declare client socket
        Socket clientSocket = null;
        try {
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.

            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            // An infinite loop to wait for incoming requests
            while(true){
                if (in.hasNextLine()) { // if there exists a request
                    // Get request
                    String requestStr = in.nextLine();
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
                    // Write sum result to socket
                    out.println(idSumMap.get(id));
                    out.flush();
                    // Print reply action
                    System.out.println("Returning sum of " + idSumMap.get(id) + " to client");
                    System.out.println();

                } else { // Ready to accept another new connection request from client
                    clientSocket = listenSocket.accept();
                    in = new Scanner(clientSocket.getInputStream());
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                }

            }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) { // Close socket if not null
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
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