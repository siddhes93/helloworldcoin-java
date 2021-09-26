package com.xingkaichun.helloworldblockchain.util;

import com.google.common.base.Strings;

/**
 * String工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class StringUtil {

    public static final String BLANKSPACE = " ";


    public static boolean equals(String string, String anotherString){
        return string.equals(anotherString);
    }

    //TODO rename
    public static boolean isNullOrEmpty(String string) {
        return Strings.isNullOrEmpty(string);
    }

    public static String prefixPadding(String rawValue,int targetLength,String paddingValue) {
        String target = rawValue;
        while (target.length() < targetLength){
            target = paddingValue + target;
        }
        return target;
    }

    public static String concatenate(String value1,String value2) {
        return value1 + value2;
    }

    public static String concatenate3(String value1, String value2, String value3) {
        return value1 + value2 + value3;
    }

    public static String valueOfUint64(long number) {
        return String.valueOf(number);
    }

    public static long length(String value) {
        return value.length();
    }
}
