package com.helloworldcoin.core.tool;

import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.SystemUtil;

/**
 * 资源路径工具类
 *
 * @author x.king xdotking@gmail.com
 */
public class ResourcePathTool {

    /**
     * 获取区块链数据存放目录
     */
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

    /**
     * 获取测试区块链数据存放目录
     */
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
