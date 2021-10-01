package com.xingkaichun.helloworldblockchain.util;

import java.util.*;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class StringsUtil {

    public static boolean hasDuplicateElement(List<String> values) {
        Set<String> set = new HashSet<>(values);
        return values.size() != set.size();
    }

    public static boolean contains(List<String> values, String value) {
        if(values == null){
            return false;
        }
        if(value == null){
            return false;
        }
        return values.contains(value);
    }

    public static List<String> split(String values, String valueSeparator) {
        if(StringUtil.isEmpty(values)){
            return new ArrayList<>();
        }
        return Arrays.asList(values.split(valueSeparator));
    }
}
