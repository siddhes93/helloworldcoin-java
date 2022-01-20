package com.helloworldcoin.crypto;

import java.security.MessageDigest;

/**
 * RipeMD160消息摘要工具类
 *
 * @author x.king xdotking@gmail.com
 */
public class Ripemd160Util {

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    /**
     * RipeMD160消息摘要
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
