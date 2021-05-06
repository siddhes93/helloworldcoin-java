package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * Sha256消息摘要工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class SHA256Util {

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    /**
     * Sha256消息摘要
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
     * 获取进行两次Sha256操作的消息摘要
     * 相比单次哈希，双重哈希更安全，双重哈希修复了单次哈希的漏洞，例如长度扩展攻击。
     * 使用双重哈希，不需要考虑单次哈希的漏洞(长度扩展攻击等)，又废不了几个算力，
     * 所以在一般情况下，我们不容易区分到底这个地方使用单次哈希，会有长度扩展等攻击吗？
     * 在这种情况下，无脑使用双重哈希不香吗？
     */
    public static byte[] doubleDigest(byte[] input) {
        return digest(digest(input));
    }
}
