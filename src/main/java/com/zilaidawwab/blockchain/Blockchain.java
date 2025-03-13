package com.zilaidawwab.blockchain;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Blockchain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 4; // these are the number of zero we want in our hash

    public static void main(String[] args) {

        // adding blocks to the blockchain array list
        blockchain.add(new Block("Hi I am the first block", "0"));
        System.out.println("Mining Block 1...");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Hey I am the second block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Mining Block 2...");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Yoh I am the third block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Mining Block 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is valid + " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }

    // This method will loop through all blocks in the chain and compare the hashes. Also checks if the hash variable
    // is actually equal to the calculated hash, and the previous block’s hash is equal to the previousHash variable.
    // Any change in blockchain block will cause this method to return false
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String targetHash = new String(new char[difficulty]).replace("\0", "0");

        // loop through blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // compare registered hash and calculated hash
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes are not Equal");
                return false;
            }

            // compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(previousBlock.calculateHash())) {
                System.out.println("Previous Hashes are not Equal");
                return false;
            }

            // check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(targetHash)) {
                System.out.println("The block hasn't been mined");
                return false;
            }
        }
        return true;
    }

    // adding new block
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
