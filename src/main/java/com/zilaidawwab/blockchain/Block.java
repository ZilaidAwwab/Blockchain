package com.zilaidawwab.blockchain;

import java.util.Date;

public class Block {

    public String hash;
    public final String previousHash;
    private final String data; // this would be a simple message in our case
    private final long timeStamp; // in milliseconds (since 1/1/1970)

    // Constructing a simple Block of Blockchain
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return Util.applySha256(previousHash + Long.toString(timeStamp) + data);
    }
}
