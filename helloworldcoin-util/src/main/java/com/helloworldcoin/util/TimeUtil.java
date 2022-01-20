package com.helloworldcoin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class TimeUtil {

    public static long millisecondTimestamp(){
        return System.currentTimeMillis();
    }

    public static String formatMillisecondTimestamp(long millisecondTimestamp) {
        Date date = new Date(millisecondTimestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.format(date);
    }
}
