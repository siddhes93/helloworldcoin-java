package com.xingkaichun.helloworldblockchain.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 系统工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SystemUtil {

    //TODO 慎重调用
    public static void errorExit(String message, Exception exception) {
        LogUtil.error("system error occurred, and exited, please check the error！"+message,exception);
        System.exit(1);
    }

    public static String systemRootDirectory() {
        Path currentWorkingDir = Paths.get("").toAbsolutePath();
        return currentWorkingDir.getParent().normalize().toString();
    }
}
