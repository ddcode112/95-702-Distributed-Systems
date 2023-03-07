/**
 * This program implements a TCP server.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 24, 2023
 */

// Import the necessary packages
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.TreeMap;

/** VerifyingServerTCP.java provides capabilities to verify messages.
 *  VerifyingServerTCP has two private members: RSA e and n.
 *
 *  For verification: the object is constructed with keys (e and n). The verify
 *  method is called with two parameters - the string to be checked and the
 *  evidence that this string was indeed manipulated by code with access to the
 *  private key d.
 */

public class VerifyingServerTCP {
    // RSA keys
    private BigInteger e,n;
    // TreeMap to store the sum for each id
    static TreeMap<BigInteger, Integer> idSumMap = null;
    //
    static private final int hash_length_id = 20;

    /** For verifying, a SignOrVerify object may be constructed
     *  with a RSA's e and n. Only e and n are used for signature verification.
     */
    public VerifyingServerTCP (BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }
    // Code refer to ShortMessageVerify.java
    /**
     * Verifying proceeds as follows:
     * 1) Decrypt the encryptedHash to compute a decryptedHash
     * 2) Hash the messageToCheck using SHA-256 (be sure to handle
     *    the extra byte as described in the signing method.)
     * 3) If this new hash is equal to the decryptedHash, return true else false.
     *
     * @param messageToCheck  a normal string (4 hex digits) that needs to be verified.
     * @param encryptedHashStr integer string - possible evidence attesting to its origin.
     * @return true or false depending on whether the verification was a success
     * @throws Exception
     */
    private boolean verify(String messageToCheck, String encryptedHashStr) throws Exception  {
        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedHashStr);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);

        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");

        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);

        // messageToCheckDigest is a full SHA-256 digest
        // take two bytes from SHA-256 and add a zero byte
        byte[] extraByte = new byte[messageToCheckDigest.length + 1];
        extraByte[0] = 0;
        for (int i = 1; i < messageToCheckDigest.length; i++) {
            extraByte[i] = messageToCheckDigest[i - 1];
        }

        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);

        // inform the client on how the two compare
        if(bigIntegerToCheck.compareTo(decryptedHash) == 0) {
            System.out.println("Signature Verified: Pass" );
            System.out.println("Clear Message: " + messageToCheck);
            System.out.println("Signed Message: " + encryptedHashStr);
            return true;
        }
        else {
            System.out.println("Signature Verified: Fail" );
            return false;
        }
    }

    /**
     * Verify if the public key hash to the ID
     * @param id id passed by client
     * @param publicKey e+n
     * @return true if the public key hash to the ID, and false otherwise
     */
    private boolean verifyID(BigInteger id, String publicKey) {
        // compute the digest with SHA-256
        byte[] bytesOfMessage;
        try {
            bytesOfMessage = publicKey.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash_value = md.digest(bytesOfMessage);
            byte[] id_byte = new byte[hash_length_id];
            for(int i = 0; i < hash_length_id; i++){
                id_byte[hash_length_id-i-1] = hash_value[hash_value.length - i - 1];
            }
            BigInteger calculatedID = new BigInteger(id_byte);
            if (calculatedID.compareTo(id) == 0) {
                System.out.println("ID Verified: Pass" );
                return true;
            } else {
                System.out.println("ID Verified: Fail" );
                return false;
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static void main(String args[]) throws Exception {
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


            VerifyingServerTCP verifySig = null;
            // An infinite loop to wait for incoming requests
            while (true) {
                if (in.hasNextLine()) { // if there exists a request
                    // Get request
                    String requestStr = in.nextLine();
                    // Split the requestStr to array [clear message, signature]
                    String[] m = requestStr.split(";");
                    // Split the clear message to array [id, choice, operand, e, n]
                    String[] element = m[0].split(",");
                    // Get the elements
                    BigInteger id = new BigInteger(element[0]);
                    int choice = Integer.parseInt(element[1]);
                    int num = Integer.parseInt(element[2]);
                    BigInteger e = new BigInteger(element[3]);
                    BigInteger n = new BigInteger(element[4]);
                    verifySig = new VerifyingServerTCP(e, n);
                    String publicKey = String.valueOf(e) + String.valueOf(n);
                    // Verify the request
                    if (element.length == 5 && verifySig.verifyID(id, publicKey) && verifySig.verify(m[0], m[1])) {
                        // Add id to map if the id hasn't requested before
                        if (!idSumMap.containsKey(id)) {
                            idSumMap.put(id, 0);
                        }
                        System.out.println("ID: " + id);
                        if (choice == 1) { // if the choice is adding
                            // Call add method
                            add(id, num);
                        } else if (choice == 2) { // if the choice is subtracting
                            // Call subtract method
                            subtract(id, num);
                        } else { // Print getting action
                            System.out.println("Getting sum...");
                        }
                        // Write sum result to socket
                        out.println(idSumMap.get(id));
                        out.flush();
                        // Print reply action
                        System.out.println("Returning sum of " + idSumMap.get(id) + " to client");
                        System.out.println();
                    } else { // Reply error if the request is not valid
                        out.println("Error in request");
                        out.flush();
                        // Print reply action
                        System.out.println("Error in request");
                        System.out.println();
                    }
                } else { // Ready to accept another new connection request from client
                    clientSocket = listenSocket.accept();
                    in = new Scanner(clientSocket.getInputStream());
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                }

            }
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
    public static int add (BigInteger id, int num) {
        // Print current adding action
        System.out.println("Adding " + num + " to " + idSumMap.get(id));
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
    public static int subtract (BigInteger id, int num) {
        // Print current subtracting action
        System.out.println("Subtracting " + num + " to " + idSumMap.get(id));
        // Subtract num to sum
        idSumMap.put(id, idSumMap.get(id) - num);
        return idSumMap.get(id);
    }

}