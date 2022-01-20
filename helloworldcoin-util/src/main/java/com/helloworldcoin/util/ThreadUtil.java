package com.helloworldcoin.util;

/**
 *
 * @author x.king xdotking@gmail.com
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
