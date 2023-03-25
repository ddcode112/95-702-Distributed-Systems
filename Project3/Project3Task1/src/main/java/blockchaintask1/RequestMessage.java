/**
 * This program implements a TCP client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Mar 15, 2023
 */
package blockchaintask1;

import com.google.gson.Gson;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class RequestMessage {
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
    // menu
    private static final String[] menu = {"View basic blockchain status.",
            "Add a transaction to the blockchain.",
            "Verify the blockchain.",
            "View the blockchain.",
            "Corrupt the chain.",
            "Hide the corruption by repairing the chain.",
            "Exit"};
    // user selection
    int selection;
    // block index
    int index;
    // transaction
    String data;
    // // an int that specifies the minimum number of left most hex digits needed by a proper hash
    int difficulty;

    /**
     * Constructor.
     */
    RequestMessage() {
        this.selection = -1;
        this.data = "";
        this.difficulty = 0;
    }
    /**
     * Implement a TCP client.
     * @param args Array of strings giving message contents and server hostname
     */
    public static void main(String[] args) {
        // Announce the client starts running
        System.out.println("Blockchain client running.");
        // Get the server side port number from user
        // For this project, use 6789
        Scanner readInput = new Scanner(System.in);
        System.out.println("Please enter server port: ");
        serverPort = readInput.nextInt();
        Gson gson = new Gson();
        RequestMessage m = new RequestMessage();
        try {
            // Collect the IP address
            aHost = InetAddress.getByName("localhost");
            // Initialize socket
            clientSocket = new Socket(aHost, serverPort);
            Scanner scn = new Scanner(System.in);
            // Continue to get user selection until user selects to quit
            while (m.selection != 6) {
                m = new RequestMessage();
                // Display menu
                for (int i = 0; i < menu.length; i++) {
                    System.out.print(i + ". ");
                    System.out.println(menu[i]);
                }
                // Get user selection
                m.selection = scn.nextInt();
                switch (m.selection) {
                    case 1 -> {  // If user selects to view the blockchain status
                        System.out.println("Enter difficulty > 0");
                        m.difficulty = scn.nextInt();
                        System.out.println("Enter transaction");
                        scn.nextLine();
                        m.data = scn.nextLine();
                    }
                    case 4 -> { // If user selects to add a block
                        System.out.println("corrupt the Blockchain");
                        System.out.println("Enter block ID of block to corrupt");
                        m.index = scn.nextInt();
                        System.out.println("Enter new data for block " + m.index);
                        scn.nextLine();
                        m.data = scn.nextLine();
                    }
                    default -> {
                    }
                }
                // Send request to server except user selects to quit
                if (m.selection != 6) parseRequest(m.selection, gson.toJson(m));
            }
            scn.close();

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

    public static void parseRequest (int selection, String requestStr) {
        // int result to record the reply sum
        ResponseMessage r;
        Gson gson = new Gson();
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            // Write request to server
            out.println(requestStr);
            out.flush();
            r = gson.fromJson(in.readLine(), ResponseMessage.class); // read a line of data from the stream
            // Handle IO exceptions
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        switch (selection) {
            case 0 -> { // If user selects to view the blockchain status
                System.out.println("Current size of chain: " + r.size);
                System.out.println("Difficulty of most recent block: " + r.diff);
                System.out.println("Total difficulty for all blocks: " + r.totalDiff);
                System.out.println("Approximate hashes per second on this machine: " + r.hps);
                System.out.println("Expected total hashes required for the whole chain: " + r.totalHashes);
                System.out.println("Nonce for most recent block: " + r.recentNonce);
                System.out.println("Chain hash: " + r.chainHash);
            }
            // Print the response if user selects to add a block, corrupt the chain, or repair the chain
            case 1, 4, 5 -> System.out.println(r.response);
            case 2 -> { // // If user selects to verify the blockchain
                System.out.print("Chain verification: ");
                if (!r.errorM.equals("TRUE")) {
                    System.out.println("FALSE");
                }
                System.out.println(r.errorM);
                System.out.println(r.response);
            }
            case 3 -> { // If user selects to view the blockchain
                System.out.println("View the Blockchain");
                System.out.println(r.response);
            }
            default -> {
            }
        }
    }
}
