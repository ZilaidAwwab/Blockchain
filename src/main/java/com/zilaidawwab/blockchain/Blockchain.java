package com.zilaidawwab.blockchain;

public class Blockchain {
    public static void main(String[] args) {
        Block firstBlock = new Block("Hi it's my first block", "0");
        System.out.println("Hash for first block: " + firstBlock.hash);

        Block secondBlock = new Block("Hi it's my second block", firstBlock.hash);
        System.out.println("Hash for second block: " + secondBlock.hash);

        Block thirdBlock = new Block("Hi it's my third block", secondBlock.hash);
        System.out.println("Hash for third block: " + thirdBlock.hash);
    }
}
