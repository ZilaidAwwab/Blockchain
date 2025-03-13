package com.zilaidawwab.blockchain;

import java.util.Date;

public class Block {

    public String hash;
    public final String previousHash;
    private final String data; // this would be a simple message in our case
    private final long timeStamp; // in milliseconds (since 1/1/1970)
    private int nonce;

    // Constructing a simple Block of Blockchain
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    // calculating the hash based on blocks content
    public String calculateHash() {
        return Util.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
    }

    // Miners will be required to do proof-of-work by trying different variable values in the block until its hash
    // starts with a certain number of 0’s (that number is passed in this method as difficulty)
    public void mineBlock(int difficulty) {
        // Creates a string with difficulty * "0"
        String target = new String(new char[difficulty]).replace("\0", "0");

        // Increment nonce until hash satisfies difficulty requirement
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++; // Change nonce to get a new hash
            hash = calculateHash(); // Recalculating the hash
        }
        System.out.println("Block Mined: " + hash);
    }
}
