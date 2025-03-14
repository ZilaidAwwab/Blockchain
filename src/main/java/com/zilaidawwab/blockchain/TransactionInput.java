/**
 * For you to own 1 bitcoin, you have to receive 1 Bitcoin. The ledger doesn't really add
 * one bitcoin to you and minus one bitcoin from the sender, the sender referenced that
 * he/she previously received one bitcoin, then a transaction output was created showing
 * that 1 Bitcoin was sent to your address. (Transaction inputs are references to previous
 * transaction outputs.).
 *
 * Your wallets balance is the sum of all the unspent transaction outputs addressed to you
 */

package com.zilaidawwab.blockchain;

public class TransactionInput {

    public String transactionOutputId; // reference to transactionOutputs -> transactionInputs
    public TransactionOutput UTXO; // contains the unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
