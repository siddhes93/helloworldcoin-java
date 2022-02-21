package com.helloworldcoin.crypto;

import org.bitcoinj.core.Base58;

/**
 * base58 util
 *
 * @author x.king xdotking@gmail.com
 */
public class Base58Util {

    /**
     * base58 encoding
     */
    public static String encode(byte[] input) {
        return Base58.encode(input);
    }

    /**
     * base58 decoding
     */
    public static byte[] decode(String input) {
        return Base58.decode(input);
    }
}