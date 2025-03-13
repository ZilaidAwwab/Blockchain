package com.zilaidawwab.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
}
