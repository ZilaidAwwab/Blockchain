// This class holds the public and private key of the user
// Public key is the user address, which could be shared to receive payments
// Private key is used to sign the transaction (this should be kept secret)

package com.zilaidawwab.blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a keyPair
            keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
