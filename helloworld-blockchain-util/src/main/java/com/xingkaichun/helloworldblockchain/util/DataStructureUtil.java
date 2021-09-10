package com.xingkaichun.helloworldblockchain.util;

import java.util.*;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class DataStructureUtil {

    public static boolean isExistDuplicateElement(List<String> list) {
        Set<String> set = new HashSet<>(list);
        return list.size() != set.size();
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
        if(StringUtil.isNullOrEmpty(values)){
            return new ArrayList<>();
        }
        return Arrays.asList(values.split(valueSeparator));
    }
}
