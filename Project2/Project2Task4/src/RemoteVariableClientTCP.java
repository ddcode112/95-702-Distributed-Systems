/**
 * This program implements a TCP client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 22, 2023
 */

// Import the necessary packages for TCP
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class RemoteVariableClientTCP {
    // Declare a client socket
    static Socket clientSocket = null;
    // Declare a BufferedReader to read from client socket
    static BufferedReader in = null;
    // Declare a PrintWriter to write to client socket
    static PrintWriter out = null;
    // Destination server port number
    static int serverPort;
    // Host name
    static InetAddress aHost;
    /**
     * Implement a TCP client.
     * @param args Array of strings giving message contents and server hostname
     */
    public static void main(String args[]) {
        // Announce the client starts running
        System.out.println("The TCP client is running.");
        // Get the server side port number from user
        // For this project, use 6789
        Scanner readInput = new Scanner(System.in);
        System.out.println("Please enter server port: ");
        serverPort = readInput.nextInt();
        System.out.println();
        try {
            // Collect the IP address
            aHost = InetAddress.getByName("localhost");
            // Initialize socket
            clientSocket = new Socket(aHost, serverPort);
            // Initialize choice
            int choice = 0;
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

        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                // Close socket if not null
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }

    public static int parseRequest (String requestStr) {
        // int result to record the reply sum
        String result;
        // Convert requestStr into byte array
        byte[] m = requestStr.getBytes();
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            // Write request to server
            out.println(requestStr);
            out.flush();
            result = in.readLine(); // read a line of data from the stream
            // Handle IO exceptions
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(result);
    }
}