package com.zilaidawwab.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Block {

    public String hash;
    public final String previousHash;
    public String merkleRoot;
    // private final String data; // this would be a simple message in our case
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // our data will be simple message
    private final long timeStamp; // in milliseconds (since 1/1/1970)
    private int nonce;

    // Constructing a simple Block of Blockchain
    public Block(String previousHash) {
        // this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); // this should be declared after other values in constructor
    }

    // calculating the hash based on blocks content
    public String calculateHash() {
        return Util.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    // Miners will be required to do proof-of-work by trying different variable values in the block until its hash
    // starts with a certain number of 0’s (that number is passed in this method as difficulty)
    public void mineBlock(int difficulty) {
        merkleRoot = Util.getMerkleRoot(transactions);
        // Creates a string with difficulty * "0"
        String target = Util.getDifficultyString(difficulty);

        // Increment nonce until hash satisfies difficulty requirement
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++; // Change nonce to get a new hash
            hash = calculateHash(); // Recalculating the hash
        }
        System.out.println("Block Mined: " + hash);
    }

    // Add transaction to this block
    public boolean addTransaction(Transaction transaction) {
        // process transaction and check if valid, unless block is first block, then ignore
        if (transaction == null) return false;
        if (!Objects.equals(previousHash, "0")) { // if previousHash != "0"
            if (!transaction.processTransaction()) {
                System.out.println("Transaction Failed to Process. Discarded");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block.");
        return true;
    }
}
