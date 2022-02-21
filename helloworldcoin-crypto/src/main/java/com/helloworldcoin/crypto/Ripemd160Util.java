package com.helloworldcoin.crypto;

import java.security.MessageDigest;

/**
 * RipeMD160 message digest tool
 *
 * @author x.king xdotking@gmail.com
 */
public class Ripemd160Util {

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    /**
     * RipeMD160 message digest
     */
    public static byte[] digest(byte[] input) {
        try {
            MessageDigest ripeMD160MessageDigest = MessageDigest.getInstance("RipeMD160",JavaCryptographyExtensionProviderUtil.getBouncyCastleProviderName());
            byte[] ripeMD160Digest = ripeMD160MessageDigest.digest(input);
            return ripeMD160Digest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
