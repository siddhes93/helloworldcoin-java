package com.xingkaichun.helloworldblockchain.util;

import java.awt.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class SystemUtil {

    public static boolean isWindowsOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isMacOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean isLinuxOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static void errorExit(String message, Exception exception) {
        LogUtil.error("system error occurred, and exited, please check the error！"+message,exception);
        System.exit(1);
    }

    public static String systemRootDirectory() {
        Path currentWorkingDir = Paths.get("").toAbsolutePath();
        return currentWorkingDir.getParent().normalize().toString();
    }

    public static void callDefaultBrowser(String url){
        try {
            //default solution : open browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
            if(isWindowsOperateSystem()){
                Runtime rt = Runtime.getRuntime();
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            }else if(isMacOperateSystem()){
                Runtime rt = Runtime.getRuntime();
                rt.exec("open " + url);
            }else {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("xdg-open " + url);
            }
        } catch (Exception e) {
            LogUtil.error("system not support call default browser.",e);
        }
    }

}
