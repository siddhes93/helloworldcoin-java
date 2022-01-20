package com.helloworldcoin.util;

import org.slf4j.LoggerFactory;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class LogUtil {

    public static void error(String message, Exception exception) {
        StackTraceElement stackTraceElement = getStackTraceElement();
        LoggerFactory.getLogger(stackTraceElement.getClassName()).error("["+stackTraceElement.getLineNumber()+"] - "+message,exception);
    }

    public static void debug(String message) {
        StackTraceElement stackTraceElement = getStackTraceElement();
        LoggerFactory.getLogger(stackTraceElement.getClassName()).debug("["+stackTraceElement.getLineNumber()+"] - "+message);
    }

    private static StackTraceElement getStackTraceElement(){
        StackTraceElement[] classArray= new Exception().getStackTrace() ;
        return classArray[2];
    }
}
