package com.xingkaichun.helloworldblockchain.core.tool;

import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;

/**
 * 资源路径工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ResourcePathTool {

    /**
     * 获取区块链数据存放目录
     */
    public static String getDataRootPath() {
        String dataRootPath;
        if(SystemUtil.isWindowsOperateSystem()){
            dataRootPath = "C:\\helloworld-blockchain-java\\";
        }else if(SystemUtil.isMacOperateSystem()){
            dataRootPath = "/tmp/helloworld-blockchain-java/";
        }else if(SystemUtil.isLinuxOperateSystem()){
            dataRootPath = "/tmp/helloworld-blockchain-java/";
        }else{
            dataRootPath = "/tmp/helloworld-blockchain-java/";
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
            dataRootPath = "C:\\helloworld-blockchain-java-test\\";
        }else if(SystemUtil.isMacOperateSystem()){
            dataRootPath = "/tmp/helloworld-blockchain-java-test/";
        }else if(SystemUtil.isLinuxOperateSystem()){
            dataRootPath = "/tmp/helloworld-blockchain-java-test/";
        }else{
            dataRootPath = "/tmp/helloworld-blockchain-java-test/";
        }
        FileUtil.makeDirectory(dataRootPath);
        return dataRootPath;
    }
}
