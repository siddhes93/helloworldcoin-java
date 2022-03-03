package com.helloworldcoin.core.tool;

import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.SystemUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class ResourceTool {

    public static String getDataRootPath() {
        String dataRootPath;
        if(SystemUtil.isWindowsOperateSystem()){
            dataRootPath = "C:\\helloworldcoin-java\\";
        }else if(SystemUtil.isMacOperateSystem()){
            dataRootPath = "/tmp/helloworldcoin-java/";
        }else if(SystemUtil.isLinuxOperateSystem()){
            dataRootPath = "/tmp/helloworldcoin-java/";
        }else{
            dataRootPath = "/tmp/helloworldcoin-java/";
        }
        FileUtil.makeDirectory(dataRootPath);
        return dataRootPath;
    }

    public static String getTestDataRootPath() {
        String dataRootPath;
        if(SystemUtil.isWindowsOperateSystem()){
            dataRootPath = "C:\\helloworldcoin-java-test\\";
        }else if(SystemUtil.isMacOperateSystem()){
            dataRootPath = "/tmp/helloworldcoin-java-test/";
        }else if(SystemUtil.isLinuxOperateSystem()){
            dataRootPath = "/tmp/helloworldcoin-java-test/";
        }else{
            dataRootPath = "/tmp/helloworldcoin-java-test/";
        }
        FileUtil.makeDirectory(dataRootPath);
        return dataRootPath;
    }
}
