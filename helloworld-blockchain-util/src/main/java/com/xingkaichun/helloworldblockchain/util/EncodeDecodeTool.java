package com.xingkaichun.helloworldblockchain.util;

/**
 * EncodeDecode工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class EncodeDecodeTool {

    public static<T> byte[] encode(T object) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(object));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static<T> T decode(byte[] bytesObject, Class<T> classOfT) {
        try {
            return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesObject),classOfT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
