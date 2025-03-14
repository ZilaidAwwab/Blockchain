/**
 * Each Transaction carries a certain amount of data
 * The public key of sender of funds
 * The public key of receiver of funds
 * The amount transferred
 * Inputs that are references to previous transactions that prove the sender has funds to send
 * Outputs, which shows the amount relevant addresses received in the transaction (These outputs are referenced as inputs in new transactions)
 * A cryptographic signature, that proves the owner of the address is the one sending this transaction and that the data hasn't been changed (this prevents third part from changing the amount sent)
 */
package com.zilaidawwab.blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionID; // this is the hash of the transaction
    public PublicKey sender; // sender's public key
    public PublicKey recipient; // recipient's public key
    public float value; // amount of fund
    public byte[] signature; // this prevents anybody else from sending funds

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // a rough count of how many transactions have been generated

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Transaction signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent)
        for (TransactionInput i : inputs) {
            i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
        }

        // check if transaction is valid
        if (getInputsValue() < Blockchain.minimumTransaction) {
            System.out.println("Transaction input too small: " + getInputsValue());
            return false;
        }

        // generate transaction outputs
        float leftOver = getInputsValue() - value; // get value of inputs then the left-over change
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionID)); // send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID)); // send the left-over back to sender

        // add outputs to unspent list
        for (TransactionOutput o : outputs) {
            Blockchain.UTXOs.put(o.id, o);
        }

        // remove transaction inputs from UTXO list as spent
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; // if transaction can't be found, skip it
            Blockchain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    // Signatures will be verified by miners as a new transaction are added to a block

    // Sign all the data we don't wish to be tampered with
    public void generateSignature(PrivateKey privateKey) {
        String data = Util.getStringFromKey(sender) + Util.getStringFromKey(recipient) + Float.toString(value);
        signature = Util.applyECDSASig(privateKey, data);
    }

    // Verify the data we signed hasn't been tempered
    public boolean verifySignature() {
        String data = Util.getStringFromKey(sender) + Util.getStringFromKey(recipient) + Float.toString(value);
        return Util.verifyECDSASig(sender, data, signature);
    }

    // This calculates the transaction hash (which will be used as its id)
    private String calculateHash() {
        sequence++; // increasing sequence so to avoid 2 identical transactions having same hash
        return Util.applySha256(
                Util.getStringFromKey(sender) +
                        Util.getStringFromKey(recipient) +
                        Float.toString(value) +
                        sequence);
    }

    // returns the sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; // if transaction is not found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    // returns sum of outputs
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}
