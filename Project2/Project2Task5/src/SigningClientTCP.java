/**
 * This program implements a TCP client.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 24, 2023
 */

// Import the necessary packages
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

/** SigningClientTCP.java provides capabilities to sign add, subtract, or get requests.
 *
 *  For signing: the SigningClientTCP object is constructed with RSA
 *  keys (e,d,n). These keys are randomly created with 2048 bits.
 *  Then, a caller can sign a message - the string returned by the sign
 *  method is evidence that the signer has the associated private key.
 */


public class SigningClientTCP {
    // RSA keys
    private BigInteger e,d,n;
    // Declare a client socket
    static Socket clientSocket = null;
    // Length to take for calculating ID
    static private final int hash_length_id = 20;
    // Declare a BigInteger id
    static private BigInteger id;
    // Declare a Scanner to read input from console
    static Scanner readInput = null;
    // Declare a BufferedReader to read from client socket
    static BufferedReader in = null;
    // Declare a PrintWriter to write to client socket
    static PrintWriter out = null;
    // Destination server port number
    static int serverPort;
    // Host name
    static InetAddress aHost;

    /** A ShortMessageSign object may be constructed with RSA's e, d, and n.
     *  The holder of the private key (the signer) would call this
     *  constructor. Only d and n are used for signing.
     */
    public SigningClientTCP (BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }




    public static void main(String args[]) throws Exception {
        // Announce the client starts running
        System.out.println("The TCP client is running.");
        // Get the server side port number from user
        // For this project, use 6789
        readInput = new Scanner(System.in);
        System.out.println("Please enter server port: ");
        // Get server port
        serverPort = readInput.nextInt();
        System.out.println();
        // Collect the IP address
        aHost = InetAddress.getByName("localhost");
        // Initialize socket
        clientSocket = new Socket(aHost, serverPort);
        // Code refer to RSAExample.java
        // Each public and private key consists of an exponent and a modulus
        BigInteger n; // n is the modulus for both the private and public keys
        BigInteger e; // e is the exponent of the public key
        BigInteger d; // d is the exponent of the private key

        Random rnd = new Random();

        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again and you will
        // notice it takes much longer to do the math with that many bits.
        BigInteger p = new BigInteger(2048,100,rnd);
        BigInteger q = new BigInteger(2048,100,rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger ("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);
        String publicKey = "(" + e + "," + n + ")";
        String privateKey = "(" + d + "," + n + ")";
        System.out.println("Public Key (e,n): " + publicKey);  // Step 6: (e,n) is the RSA public key
        System.out.println("Private Key (d,n): " + privateKey);  // Step 7: (d,n) is the RSA private key

        SigningClientTCP sov = new SigningClientTCP(e,d,n);
        // Generate id for the current session
        sov.generateID(String.valueOf(e) + String.valueOf(n));
        // Declare a request str to send
        String requestStr = "";
        // Declare a reply str for the reply
        String reply;

        while (true) {
            // Get the choice from user
            int choice = sov.getChoice();
            if (choice != 4) { // Choices other than quitting
                // Get the requestStr including id, choice, operand, public key, signature
                requestStr = sov.getRequestStr(choice, e, n);
                // Print the message before signing
                System.out.println("Clear Message: " + requestStr);
                // Sign the request
                String signedVal = sov.sign(requestStr);
                // Print the signature
                System.out.println("Signed Message: " + signedVal);
                // Concatenate the message and the signature
                requestStr = requestStr + ";" + signedVal;
                // Send request to server
                reply = sov.send(requestStr);
                // Print result to console
                System.out.println("The result is " + reply + ".");
                System.out.println();
            } else { // Client requests quiiting
                System.out.println("Client side quitting. The remote variable server is still running.");
                break;
            }
        }

    }

    /**
     * Hash function using SHA-256
     * @param hashStr
     * @return the hashed string in byte array
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private byte[] h(String hashStr) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // compute the digest with SHA-256
        byte[] bytesOfMessage = hashStr.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);
        return bigDigest;
    }

    /**
     * Generate a unique id with the last 20 byte of the public key
     * @param publicKey e+n
     */
    private void generateID(String publicKey){
        try {
            // Get the hashed value
            byte[] hash_value = h(publicKey);
            // Get the last 20 bytes
            byte[] id_byte = new byte[hash_length_id];
            for(int i = 0; i < hash_length_id; i++){
                id_byte[hash_length_id-i-1] = hash_value[hash_value.length - i - 1];
            }
            id = new BigInteger(id_byte);
            System.out.println("ID: " + id);
        } catch(NoSuchAlgorithmException e) {
            System.out.println("No Hash available" + e);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

    }
    // Code refer to ShortMessageSign.java
    /**
     * Signing proceeds as follows:
     * 1) Get the bytes from the string to be signed.
     * 2) Compute a SHA-1 digest of these bytes.
     * 3) Copy these bytes into a byte array that is one byte longer than needed.
     *    The resulting byte array has its extra byte set to zero. This is because
     *    RSA works only on positive numbers. The most significant byte (in the
     *    new byte array) is the 0'th byte. It must be set to zero.
     * 4) Create a BigInteger from the byte array.
     * 5) Encrypt the BigInteger with RSA d and n.
     * 6) Return to the caller a String representation of this BigInteger.
     * @param message a sting to be signed
     * @return a string representing a big integer - the encrypted hash.
     * @throws Exception
     */
    public String sign(String message) throws Exception {
        // Get the hashed message
        byte[] bigDigest = h(message);

        // Get the signed value
        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        byte[] messageDigest = new byte[bigDigest.length + 1];
        messageDigest[0] = 0;   // most significant set to 0
        for (int i = 1; i < bigDigest.length; i++) {
            messageDigest[i] = bigDigest[i-1]; // take a byte from SHA-256
        }

        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);

        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);

        // return this as a big integer string
        return c.toString();
    }

    /**
     * Get user choice.
     * 1: Add, 2: Subtract, 3: Get
     * @return choice number
     */
    private int getChoice() {
        readInput = new Scanner(System.in);
        // Initialize choice
        int choice = 0;
        // Display menu
        System.out.println("1. Add a value to your sum.");
        System.out.println("2. Subtract a value from your sum.");
        System.out.println("3. Get your sum.");
        System.out.println("4. Exit client");
        if (readInput.hasNextInt()) {
            // Get choice
            choice = readInput.nextInt();
        }
        return choice;
    }

    /**
     * Return request string in format "id,choice,operand,e,n"
     * @param choice choice number
     * @param e e
     * @param n n
     * @return full request string without signature
     */
    private String getRequestStr(int choice, BigInteger e, BigInteger n) {
        readInput = new Scanner(System.in);
        // Initialize request string
        String requestStr = "";

        // num variable to store int for add or subtract choice
        int num;

        switch (choice) {
            case 1: // If user selects to add
                System.out.println("Enter value to add: ");
                // Get num to add
                num = readInput.nextInt();
                // Concatenate request string
                requestStr = id + "," + choice + "," + num + "," + e + "," + n;
                break;
            case 2: // If user selects to subtract
                System.out.println("Enter value to subtract: ");
                // Get num to subtract
                num = readInput.nextInt();
                // Concatenate request string
                requestStr = id + "," + choice + "," + num + "," + e + "," + n;
                break;
            case 3: // If user selects to get
                // Concatenate request string
                requestStr = id + "," + choice + "," + 0 + "," + e + "," + n;
                break;
        }
        return requestStr;
    }

    /**
     * Communicate with server
     * @param requestStr request string including signature
     * @return reply from server
     */
    private String send(String requestStr) {
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
        return result;
    }
}
