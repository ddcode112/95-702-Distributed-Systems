/**
 * This program implements a TCP server.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Mar 15, 2023
 */
package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class ResponseMessage {
    // BlockChain object with an ArrayList to hold Blocks and a chain hash to hold a SHA256 hash of the most recently added Block.
    static BlockChain ds_chain;
    // user selection
    int selection;
    // size of chain
    Integer size;
    // a String to hold a SHA256 hash of the most recently added Block
    String chainHash;
    // the expected number of hashes required for the entire chain
    Double totalHashes;
    // Total difficulty of the blockchain
    Integer totalDiff;
    // a BigInteger value determined by a proof of work routine
    BigInteger recentNonce;
    // an Integer that specifies the minimum number of left most hex digits needed by a proper hash
    Integer diff;
    // the instance variable approximating the number of hashes per second
    Integer hps;
    // error message if the chain is invalid
    String errorM;
    // blockchain server response
    String response;

    public static void main(String[] args) {
        // Announce the server starts running
        System.out.println("Blockchain server running");
        // Port number this server to listen on
        int serverPort = 6789;
        // Declare client socket
        Socket clientSocket = null;
        ServerSocket listenSocket;
        ds_chain = new BlockChain();
        ds_chain.addBlock(new Block(ds_chain.getChainSize(), ds_chain.getTime(), "Genesis", 2));
        ds_chain.computeHashesPerSecond();
        try {
            // Create a new server socket
            listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.
            System.out.println("We have a visitor");
            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            Gson gson = new Gson();
            RequestMessage m;
            // An infinite loop to wait for incoming requests
            while(true){
                if (in.hasNextLine()) { // if there exists a request
                    // Get request
                    m = gson.fromJson(in.nextLine(), RequestMessage.class);
                    String responseJson = process(m.selection, m);
                    // Write sum result to socket
                    out.println(responseJson);
                    out.flush();

                } else { // Ready to accept another new connection request from client
                    clientSocket = listenSocket.accept();
                    System.out.println("We have a visitor");
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
     * Process users request and return reponse
     * @param selection user selection
     * @param m request message
     * @return response
     */
    public static String process(int selection, RequestMessage m) {
        Gson gson = new Gson();
        ResponseMessage r = new ResponseMessage();
        Timestamp start;
        Timestamp end;
        int executionTime;
        switch (selection) {
            case 0 -> { // If user selects to view the blockchain status
                r.selection = selection;
                r.size = ds_chain.getChainSize();
                r.chainHash = ds_chain.getChainHash();
                r.totalHashes = ds_chain.getTotalExpectedHashes();
                r.totalDiff = ds_chain.getTotalDifficulty();
                r.recentNonce = ds_chain.getLatestBlock().getNonce();
                r.diff = ds_chain.getLatestBlock().getDifficulty();
                r.hps = ds_chain.hashPerSecond;
                System.out.println("Response: " + gson.toJson(r));
            }
            case 1 -> { // If user selects to add a block
                r.selection = selection;
                System.out.println("Adding a block");
                start = ds_chain.getTime();
                ds_chain.addBlock(new Block(ds_chain.getChainSize(), ds_chain.getTime(), m.data, m.difficulty));
                end = ds_chain.getTime();
                executionTime = (int) (end.getTime() - start.getTime());
                String t = "Total execution time to add this block was " + executionTime + " milliseconds";
                System.out.println("Setting response to " + t);
                r.response = t;
                System.out.println("..." + gson.toJson(r));
            }
            case 2 -> { // If user selects to verify the chain
                System.out.println("Verifying entire chain");
                start = ds_chain.getTime();
                String result = ds_chain.isChainValid();
                end = ds_chain.getTime();
                executionTime = (int) (end.getTime() - start.getTime());
                System.out.print("Chain verification: ");
                if (result.equals("TRUE")) {
                    System.out.println("TRUE");
                } else {
                    System.out.println("FALSE");
                    System.out.println(result);
                }
                r.errorM = result;
                String t1 = "Total execution time to verify the chain was " + executionTime + " milliseconds";
                System.out.println(t1);
                System.out.println("Setting response to " + t1);
                r.response = t1;
            }
            case 3 -> { // If user selects to view the blockchain
                System.out.println("View the Blockchain");
                String view = ds_chain.toString();
                System.out.println("Setting response to " + view);
                r.response = view;
            }
            case 4 -> { // If user selects to corrupt the chain
                System.out.println("Corrupt the Blockchain");
                ds_chain.getBlock(m.index).setData(m.data);
                String newM = "Block " + m.index + " now holds " + m.data;
                System.out.println(newM);
                System.out.println("Setting response to " + newM);
                r.response = newM;
            }
            case 5 -> { // If user selects to repair the chain
                System.out.println("Repairing the entire chain");
                start = ds_chain.getTime();
                ds_chain.repairChain();
                end = ds_chain.getTime();
                executionTime = (int) (end.getTime() - start.getTime());
                String t2 = "Total execution time required to repair the chain was " + executionTime + " milliseconds";
                System.out.println("Setting response to " + t2);
                r.response = t2;
            }
        }
        return gson.toJson(r);
    }

    public static class BlockChain {
        // an ArrayList to hold Blocks
        @Expose private ArrayList<Block> ds_chain;
        // a String to hold a SHA256 hash of the most recently added Block
        @Expose private String chainHash;
        // the instance variable approximating the number of hashes per second
        private int hashPerSecond;
        private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        /**
         * Constructor.
         */
        BlockChain() {
            this.ds_chain = new ArrayList<>();
            this.chainHash = "";
            this.hashPerSecond = 0;
        }
        /**
         * Add a new Block to the BlockChain.
         * @param newBlock - is added to the BlockChain as the most recent block
         */
        public void addBlock(Block newBlock) {
            newBlock.setPreviousHash(this.chainHash);
            this.chainHash = newBlock.proofOfWork();
            this.ds_chain.add(newBlock);
        }
        /**
         * Computes exactly 2 million hashes and times how long that process takes.
         */
        public void computeHashesPerSecond() {
            String str = "00000000";
            Timestamp start = getTime();
            for (int i = 0; i < 2000000; i++) {
                calculateHash(str);
            }
            Timestamp end = getTime();
            this.hashPerSecond = (int) ((2000000 / (end.getTime() - start.getTime())) * 1000);
        }
        /**
         * Return block at position i.
         * @param i position
         * @return Block at position i
         */
        public Block getBlock(int i) {
            return this.ds_chain.get(i);
        }
        /**
         * Get chain hash.
         * @return chain hash
         */
        public String getChainHash() {
            return this.chainHash;
        }
        /**
         * Get the size of the chain in blocks.
         * @return the size of the chain in blocks
         */
        public int getChainSize() {
            return this.ds_chain.size();
        }
        /**
         * Get the instance variable approximating the number of hashes per second.
         * @return the instance variable approximating the number of hashes per second
         */
        public int getHashesPerSecond() {
            return this.hashPerSecond;
        }
        /**
         * Get a reference to the most recently added Block.
         * @return a reference to the most recently added Block
         */
        public Block getLatestBlock() {
            return this.ds_chain.get(this.getChainSize() - 1);
        }
        /**
         * Get the current system time.
         * @return the current system time
         */
        public Timestamp getTime() {
            return new Timestamp(System.currentTimeMillis());
        }
        /**
         * Compute and return the total difficulty of all blocks on the chain. Each block knows its own difficulty.
         * @return totalDifficulty
         */
        public int getTotalDifficulty() {
            int totalDifficulty = 0;
            for (Block block: ds_chain) {
                totalDifficulty += block.getDifficulty();
            }
            return totalDifficulty;
        }
        /**
         * Compute and return the expected number of hashes required for the entire chain.
         * @return totalExpectedHashes
         */
        public double getTotalExpectedHashes() {
            double totalExpectedHashes = 0;
            for (Block block: ds_chain) {
                totalExpectedHashes += Math.pow(16, block.getDifficulty());
            }
            return totalExpectedHashes;
        }
        /**
         * Verify if the BlockChain is valid.
         * A valid BlockChain should satisfy:
         * 1. the hash of each block has the requisite number of leftmost 0's (proof of work) as specified in the difficulty field.
         * 2. the chain hash is equal to this computed hash.
         * @return "TRUE" if the chain is valid, otherwise an error message
         */
        public String isChainValid() {
            String previousHash = "";
            for (int i = 0; i < getChainSize(); i++) {
                Block b = getBlock(i);
                String hash = b.calculateHash();
                int result = isBlockValid(b, hash, previousHash);
                if (result == -1) {
                    return "Improper hash on node " + i + "Does not begin with " + "0".repeat(b.getDifficulty());
                } else if (result == -2) {
                    return "Chain hash is not correct";
                }
                previousHash = hash;
            }
            return "TRUE";
        }
        /**
         * Repairs the chain.
         * It checks the hashes of each block and ensures that any illegal hashes are recomputed.
         * Also, it computes new proof of work based on the difficulty specified in the Block.
         */
        public void repairChain() {
            String previousHash = "";
            for (int i = 0;i < getChainSize(); i++) {
                Block b = getBlock(i);
                String hash = b.calculateHash();
                if (isBlockValid(b, hash, previousHash) != 0) {
                    if (i < getChainSize() - 1) {
                        getBlock(i + 1).setPreviousHash(b.proofOfWork());
                    } else {
                        this.chainHash = b.proofOfWork();
                    }
                }
                previousHash = hash;
            }
        }
        /**
         * Helper method of isChainValid and repairChain.
         * Verify if a Block is valid.
         * @param block Block to verify
         * @param hash hash value
         * @param previousHash previous hash
         * @return 0 if valid, -1 if not beginning with the requisite number, -2 if chain hash is incorrect
         */
        public int isBlockValid(Block block, String hash, String previousHash) {
            String proof = "0".repeat(block.getDifficulty());
            if (!hash.substring(0, block.getDifficulty()).equals(proof)) {
                return -1;
            }
            if (!block.getPreviousHash().equals(previousHash)) {
                return -2;
            }
            return 0;
        }
        /**
         * Uses the toString method defined on each individual block.
         * @return a String representation of the entire chain is returned
         */
        @Override
        public String toString() {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            return gson.toJson(this);
        }
        /**
         * Computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
         * @param toHash String to hash
         * @return a String holding Hexadecimal characters
         */
        public String calculateHash(String toHash) {
            String hash = "";
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-256");
                md.update(toHash.getBytes());
                hash =  bytesToHex(md.digest());
            } catch (NoSuchAlgorithmException e) {
                System.out.println("No hash value available" + e);
            }
            return hash;
        }
        /**
         * Returns a hex string given an array of bytes.
         * Refer to https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java.
         * @param bytes array of bytes to converted
         * @return a hex string
         */
        public static String bytesToHex(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        }
    }
}
