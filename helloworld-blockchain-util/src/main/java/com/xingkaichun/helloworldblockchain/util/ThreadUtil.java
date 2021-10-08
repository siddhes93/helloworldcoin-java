package com.xingkaichun.helloworldblockchain.util;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class ThreadUtil {

    public static void millisecondSleep(long millisecond){
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            throw new RuntimeException("sleep failed.",e);
        }
    }
}
