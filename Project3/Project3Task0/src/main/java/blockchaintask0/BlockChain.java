/**
 * This class represents a simple BlockChain.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Mar 17, 2023
 */
package blockchaintask0;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockChain {
    // an ArrayList to hold Blocks
    @Expose private ArrayList<Block> ds_chain;
    // a String to hold a SHA256 hash of the most recently added Block
    @Expose private String chainHash;
    // the instance variable approximating the number of hashes per second
    private int hashPerSecond;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    // menu
    private static final String[] menu = {"View basic blockchain status.",
                                         "Add a transaction to the blockchain.",
                                         "Verify the blockchain.",
                                         "View the blockchain.",
                                         "Corrupt the chain.",
                                         "Hide the corruption by repairing the chain.",
                                         "Exit"};

    /**
     * Constructor.
     */
    BlockChain() {
        this.ds_chain = new ArrayList<>();
        this.chainHash = "";
        this.hashPerSecond = 0;
    }
    /*
    Run times for addBlock increase as the difficulty level gets higher.
    Difficulty <= 3 typically takes less than 10 milliseconds to add a block,
    while >= 4 can take more than 100 milliseconds.
    For isChainValid, it generally takes less than 1 millisecond.
    For chainRepair, similarly, as the difficulty level gets higher,
    it will take longer, as addBlock function.
     */
    public static void main(String[] args) {
        // Initiate the BlockChain and add the first Genesis block with the difficulty of 2
        BlockChain bc = new BlockChain();
        bc.addBlock(new Block(bc.getChainSize(), bc.getTime(), "Genesis", 2));
        bc.computeHashesPerSecond();
        int choice = -1;
        Timestamp start;
        Timestamp end;
        int executionTime;
        Scanner scn = new Scanner(System.in);
        // Continue to get user selection until user selects to quit
        while (choice != 6) {
            // Display menu
            for (int i = 0; i < menu.length; i++) {
                System.out.print(i + ". ");
                System.out.println(menu[i]);
            }
            // Get user choice
            choice = scn.nextInt();
            switch (choice) {
                case 0 -> { // If user selects to view the blockchain status
                    System.out.println("Current size of chain: " + bc.getChainSize());
                    System.out.println("Difficulty of most recent block: " + bc.getLatestBlock().getDifficulty());
                    System.out.println("Total difficulty for all blocks: " + bc.getTotalDifficulty());
                    System.out.println("Approximate hashes per second on this machine: " + bc.getHashesPerSecond());
                    System.out.println("Expected total hashes required for the whole chain: " + bc.getTotalExpectedHashes());
                    System.out.println("Nonce for most recent block: " + bc.getLatestBlock().getNonce());
                    System.out.println("Chain hash: " + bc.getChainHash());
                }
                case 1 -> { // If user selects to add a block
                    System.out.println("Enter difficulty > 0");
                    int difficulty = scn.nextInt();
                    System.out.println("Enter transaction");
                    scn.nextLine();
                    String data = scn.nextLine();
                    start = bc.getTime();
                    bc.addBlock(new Block(bc.getChainSize(), bc.getTime(), data, difficulty));
                    end = bc.getTime();
                    executionTime = (int) (end.getTime() - start.getTime());
                    System.out.println("Total execution time to add this block was " + executionTime + " milliseconds");
                }
                case 2 -> { // If user selects to verify the blockchain
                    start = bc.getTime();
                    String result = bc.isChainValid();
                    end = bc.getTime();
                    executionTime = (int) (end.getTime() - start.getTime());
                    System.out.print("Chain verification: ");
                    // Print verification result
                    if (result.equals("TRUE")) {
                        System.out.println(result);
                    } else { // False with additional error message
                        System.out.println("FALSE");
                        System.out.println(result);
                    }
                    System.out.println("Total execution time to verify the chain was " + executionTime + " milliseconds");
                }
                case 3 -> { // If user selects to view the blockchain
                    System.out.println("View the Blockchain");
                    System.out.println(bc);
                }
                case 4 -> { // If user selects to corrupt the blockchain
                    System.out.println("corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int id = scn.nextInt();
                    System.out.println("Enter new data for block " + id);
                    scn.nextLine();
                    String newData = scn.nextLine();
                    bc.getBlock(id).setData(newData);
                    System.out.println("Block " + id + " now holds " + newData);
                }
                case 5 -> { // If user selects to repair the blockchain
                    start = bc.getTime();
                    bc.repairChain();
                    end = bc.getTime();
                    executionTime = (int) (end.getTime() - start.getTime());
                    System.out.println("Total execution time required to repair the chain was " + executionTime + " milliseconds");
                }
                default -> {
                }
            }
        }
        scn.close();
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
            hash = bytesToHex(md.digest());
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
