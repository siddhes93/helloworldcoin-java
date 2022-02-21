package com.helloworldcoin.crypto;

import java.security.MessageDigest;

/**
 * Sha256 message digest tool
 *
 * @author x.king xdotking@gmail.com
 */
public class Sha256Util {

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    /**
     * Sha256 message digest
     */
    public static byte[] digest(byte[] input) {
        try {
            MessageDigest sha256MessageDigest = MessageDigest.getInstance("SHA-256",JavaCryptographyExtensionProviderUtil.getBouncyCastleProviderName());
            byte[] sha256Digest = sha256MessageDigest.digest(input);
            return sha256Digest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Double Sha256 message digest
     */
    public static byte[] doubleDigest(byte[] input) {
        return digest(digest(input));
    }
}
