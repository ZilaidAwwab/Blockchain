package com.zilaidawwab.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Util {

    // Applies SHA256 algorithm to string, and returns the generated signature as a string
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Applying sha-256 to our input, it produces a 256 bit sequence
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // This will hold hash as hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Applies ECDSA Signature and returns the result (as bytes)
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] srtByte = input.getBytes();
            dsa.update(srtByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return output;
    }

    // Verify a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] sign) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(sign);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // This method is implementing Merkle Tree hashing, which is used in blockchain to create a single hash
    // representing a collection of transactions
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for (Transaction t : transactions) {
            previousTreeLayer.add(t.transactionID);
        }

        ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<String>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                // previousTreeLayer = [A, B, C, D]
                // applySha256(A + B) → Hash1
                // applySha256(C + D) → Hash2
                // treeLayer = [Hash1, Hash2]
                // [A, B, C, D]  → [Hash1, Hash2] → [RootHash]
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size(); // at the end we want a single hash, so this will be updated to 1
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    // difficulty 5 would return hash which has 5 zeros in the start "00000..."
    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace("\0", "0");
    }
}
