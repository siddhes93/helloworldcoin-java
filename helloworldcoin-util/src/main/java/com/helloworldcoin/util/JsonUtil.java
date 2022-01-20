package com.helloworldcoin.util;

import com.google.gson.Gson;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class JsonUtil {

    private static Gson GSON = new Gson();

    public static String toString(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T toObject(String json, Class<T> classOfT) {
        return GSON.fromJson(json,classOfT);
    }
}
