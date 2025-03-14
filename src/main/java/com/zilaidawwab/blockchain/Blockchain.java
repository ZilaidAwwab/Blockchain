package com.zilaidawwab.blockchain;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class Blockchain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    // This hashmap contains list of all unspent transactions
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static int difficulty = 3; // these are the number of zero we want in our hash
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction firstTransaction; // this is hardcoded in every new coin created

    public static void main(String[] args) {

        // Setup Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        // Create new wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinBase = new Wallet(); // hard-coded money

        // create first transaction that sends 100 coins to wallet A
        firstTransaction = new Transaction(coinBase.publicKey, walletA.publicKey ,100f, null);
        firstTransaction.generateSignature(coinBase.privateKey); // manually signing the first transaction
        firstTransaction.transactionID = "0"; // manually setting the transaction id
        firstTransaction.outputs.add(new TransactionOutput(firstTransaction.recipient, firstTransaction.value, firstTransaction.transactionID)); // manually adding the transaction output
        UTXOs.put(firstTransaction.outputs.get(0).id, firstTransaction.outputs.get(0)); // necessary to store first transaction in the UTXOs list.

        System.out.println("Creating and Mining First Block...");
        Block first = new Block("0");
        first.addTransaction(firstTransaction);
        addBlock(first);

        // Testing
        Block block1 = new Block(first.hash);
        System.out.println("\nWalletA's Balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (50) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 50f));
        addBlock(block1);

        System.out.println("\nWalletA's Balance is: " + walletA.getBalance());
        System.out.println("\nWalletB's Balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (100) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 100f));
        addBlock(block2);

        System.out.println("\nWalletA's Balance is: " + walletA.getBalance());
        System.out.println("\nWalletB's Balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        addBlock(block3);

        System.out.println("\nWalletA's Balance is: " + walletA.getBalance());
        System.out.println("WalletB's Balance is: " + walletB.getBalance());

        isChainValid();

        // Test public and private keys
//        System.out.println("Private and Public Keys");
//        System.out.println(Util.getStringFromKey(walletA.privateKey));
//        System.out.println(Util.getStringFromKey(walletA.publicKey));
//
//        // Creating a test transaction from wallet A to B
//        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
//        transaction.generateSignature(walletA.privateKey);
//
//        // Verify the signature works and verify it from public key
//        System.out.println("Is signature verified");
//        System.out.println(transaction.verifySignature());

        // adding blocks to the blockchain array list
//        blockchain.add(new Block("Hi I am the first block", "0"));
//        System.out.println("Mining Block 1...");
//        blockchain.get(0).mineBlock(difficulty);
//
//        blockchain.add(new Block("Hey I am the second block", blockchain.get(blockchain.size()-1).hash));
//        System.out.println("Mining Block 2...");
//        blockchain.get(1).mineBlock(difficulty);
//
//        blockchain.add(new Block("Yoh I am the third block", blockchain.get(blockchain.size()-1).hash));
//        System.out.println("Mining Block 3...");
//        blockchain.get(2).mineBlock(difficulty);
//
//        System.out.println("\nBlockchain is valid + " + isChainValid());
//
//        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println(blockchainJson);
    }

    // This method will loop through all blocks in the chain and compare the hashes. Also checks if the hash variable
    // is actually equal to the calculated hash, and the previous block’s hash is equal to the previousHash variable.
    // Any change in blockchain block will cause this method to return false
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String targetHash = new String(new char[difficulty]).replace("\0", "0");
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(firstTransaction.outputs.get(0).id, firstTransaction.outputs.get(0));

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

            // loop through blockchains transactions
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("Signature on transaction(" + t + ") is Invalid");
                    return false;
                }

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("Referenced input on Transaction(" + t + ") is missing.");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("Referenced input Transaction(" + t + ") value is invalid.");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("Transaction(" + t + ") output recipient is not who it should be.");
                    return false;
                }

                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("Transaction(" + t + ") output 'change' is not sender");
                    return false;
                }
            }
        }
        System.out.println("\nBlockchain is Valid!");
        return true;
    }

    // adding new block
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
