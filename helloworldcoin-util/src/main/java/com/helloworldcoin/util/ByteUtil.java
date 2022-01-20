package com.helloworldcoin.util;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class ByteUtil {

    public static final int BYTE8_BYTE_COUNT = 8;

    public static String bytesToHexString(byte[] bytes) {
        return Hex.toHexString(bytes);
    }
    public static byte[] hexStringToBytes(String hexString) {
        return Hex.decode(hexString);
    }



    /**
     * Returns a big-endian representation of value in an 8-element byte array;
     */
    public static byte[] uint64ToBytes(long number) {
        return Longs.toByteArray(number);
    }
    /**
     * Returns the long value whose big-endian representation is stored an 8-element byte array;
     */
    public static long bytesToUint64(byte[] bytes) {
        return Longs.fromByteArray(bytes);
    }



    public static byte[] stringToUtf8Bytes(String stringValue) {
        return stringValue.getBytes(StandardCharsets.UTF_8);
    }
    public static String utf8BytesToString(byte[] bytesValue) {
        return new String(bytesValue,StandardCharsets.UTF_8);
    }



    public static byte[] booleanToUtf8Bytes(boolean booleanValue) {
        return String.valueOf(booleanValue).getBytes(StandardCharsets.UTF_8);
    }
    public static boolean utf8BytesToBoolean(byte[] bytesValue) {
        return Boolean.valueOf(new String(bytesValue,StandardCharsets.UTF_8));
    }




    public static byte[] concatenate(byte[] bytes1,byte[] bytes2) {
        return Bytes.concat(bytes1,bytes2);
    }
    public static byte[] concatenate3(byte[] bytes1,byte[] bytes2,byte[] bytes3) {
        return Bytes.concat(bytes1,bytes2,bytes3);
    }
    public static byte[] concatenate4(byte[] bytes1,byte[] bytes2,byte[] bytes3,byte[] bytes4) {
        return Bytes.concat(bytes1,bytes2,bytes3,bytes4);
    }



    public static byte[] concatenateLength(byte[] value) {
        return concatenate(uint64ToBytes(value.length),value);
    }

    public static byte[] flat(List<byte[]> values) {
        byte[] concatBytes = new byte[0];
        for(byte[] value:values){
            concatBytes = concatenate(concatBytes,value);
        }
        return concatBytes;
    }

    public static byte[] flatAndConcatenateLength(List<byte[]> values) {
        byte[] flatBytes = flat(values);
        return concatenateLength(flatBytes);
    }


    public static byte[] copy(byte[] sourceBytes, int startPosition, int length) {
        byte[] destinationBytes = new byte[length];
        System.arraycopy(sourceBytes,startPosition,destinationBytes,0,length);
        return destinationBytes;
    }
    public static void copyTo(byte[] sourceBytes, int sourceStartPosition, int length, byte[] destinationBytes, int destinationStartPosition){
        System.arraycopy(sourceBytes,sourceStartPosition,destinationBytes,destinationStartPosition,length);
    }


    public static boolean equals(byte[] bytes1, byte[] bytes2) {
        return Arrays.equals(bytes1,bytes2);
    }

    public static byte[] random32Bytes(){
        byte[] randomBytes = new byte[32];
        Random RANDOM = new Random();
        RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }
}