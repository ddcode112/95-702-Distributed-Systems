/**
 * This class represents a simple Block.
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Mar 17, 2023
 */
package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class Block {
    // the index of this block in the chain
    @Expose private int index;
    // of when this block was created
    @Expose private Timestamp timestamp;
    // the transaction
    @Expose private String Tx;
    // the SHA256 hash of a block's parent
    @Expose private String PrevHash;
    // a BigInteger value determined by a proof of work routine
    @Expose private BigInteger nonce;
    // an int that specifies the minimum number of left most hex digits needed by a proper hash
    @Expose private int difficulty;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    /**
     * Constructor.
     * @param index the index of this block in the chain
     * @param timestamp of when this block was created
     * @param data represents the transaction held by this block
     * @param difficulty determines how much work is required to produce a proper hash
     */
    Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.Tx = data;
        this.difficulty = difficulty;
        this.nonce = BigInteger.ZERO;
    }
    /**
     * Computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * @return a String holding Hexadecimal characters
     */
    public String calculateHash() {
        String details = getIndex() + getTimestamp().toString() + getData() + getPreviousHash() + getNonce() + getDifficulty();
        String hash = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(details.getBytes());
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
    /**
     * Get this block's transaction.
     * @return this block's transaction
     */
    public String getData() {
        return this.Tx;
    }
    /**
     * Get difficulty.
     * @return difficulty
     */
    public int getDifficulty() {
        return this.difficulty;
    }
    /**
     * Get index of block.
     * @return index of block
     */
    public int getIndex() {
        return this.index;
    }
    /**
     * Returns the nonce for this block.
     * The nonce is a number that has been found to cause the hash of this block
     * to have the correct number of leading hexadecimal zeroes.
     * @return a BigInteger representing the nonce for this block.
     */
    public BigInteger getNonce() {
        return this.nonce;
    }
    /**
     * Get previous hash.
     * @return previous hash
     */
    public String getPreviousHash() {
        return this.PrevHash;
    }
    /**
     * Get timestamp of this block.
     * @return timestamp of this block
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    /**
     * Finds a good hash. It increments the nonce until it produces a good hash.
     * @return a String with a hash that has the appropriate number of leading hex zeroes.
     */
    public String proofOfWork() {
        String targetLeadingZeroes = "0".repeat(getDifficulty());
        String hash = "";
        while (true) {
            hash = calculateHash();
            if (!hash.substring(0, getDifficulty()).equals(targetLeadingZeroes)) {
                nonce = nonce.add(BigInteger.ONE);
            } else {
                break;
            }
        }
        return hash;
    }
    /**
     * Set the transaction of this block.
     * @param data - represents the transaction held by this block
     */
    public void setData(String data) {
        this.Tx = data;
    }
    /**
     * Set difficulty.
     * @param difficulty - determines how much work is required to produce a proper hash
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * Set index.
     * @param index - the index of this block in the chain
     */
    public void setIndex(int index) {
        this.index = index;
    }
    /**
     * Set previous hash.
     * @param previousHash - a hashpointer to this block's parent
     */
    public void setPreviousHash(String previousHash) {
        this.PrevHash = previousHash;
    }
    /**
     * Set block created timestamp.
     * @param timestamp - of when this block was created
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Block to String.
     * @return A JSON representation of all of this block's data is returned.
     */
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
        return gson.toJson(this);
    }
}
