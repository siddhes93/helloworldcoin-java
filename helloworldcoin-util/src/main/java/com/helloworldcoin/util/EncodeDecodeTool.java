package com.helloworldcoin.util;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class EncodeDecodeTool {

    public static<T> byte[] encode(T object) {
        return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(object));
    }
    public static<T> T decode(byte[] bytesObject, Class<T> objectClass) {
        return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesObject),objectClass);
    }
}
