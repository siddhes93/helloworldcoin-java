package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class CoreConfigurationDefaultImpl extends CoreConfiguration {

    //BlockchainCore数据存放路径
    private final String corePath;
    //配置数据库名字
    private static final String CONFIGURATION_DATABASE_NAME = "ConfigurationDatabase";

    //'矿工是否是激活状态'存入到数据库时的主键
    private static final String MINE_OPTION_KEY = "IS_MINER_ACTIVE";

    //'矿工可挖的最高区块高度'存入到数据库时的主键
    private static final String MINE_MAX_BLOCK_HEIGHT_KEY = "MAX_BLOCK_HEIGHT";



    //'矿工是否是激活状态'的默认值
    private static final boolean MINE_OPTION_DEFAULT_VALUE = false;

    //这个时间间隔更新一次正在被挖矿的区块的交易。如果时间太长，可能导致新提交的交易延迟被确认。
    public static final long MINE_TIMESTAMP_PER_ROUND = 1000 * 10;

    public CoreConfigurationDefaultImpl(String corePath) {
        FileUtil.makeDirectory(corePath);
        this.corePath = corePath;
    }



    @Override
    public String getCorePath() {
        return corePath;
    }

    @Override
    public boolean isMinerActive() {
        byte[] mineOption = getConfigurationValue(ByteUtil.stringToUtf8Bytes(MINE_OPTION_KEY));
        if(mineOption == null){
            return MINE_OPTION_DEFAULT_VALUE;
        }
        return Boolean.parseBoolean(ByteUtil.utf8BytesToString(mineOption));
    }

    @Override
    public void activeMiner() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(MINE_OPTION_KEY),ByteUtil.stringToUtf8Bytes(String.valueOf(Boolean.TRUE)));
    }

    @Override
    public void deactiveMiner() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(MINE_OPTION_KEY),ByteUtil.stringToUtf8Bytes(String.valueOf(Boolean.FALSE)));
    }

    @Override
    public void setMaxBlockHeight(long maxHeight) {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(MINE_MAX_BLOCK_HEIGHT_KEY),ByteUtil.stringToUtf8Bytes(String.valueOf(maxHeight)));
    }

    @Override
    public long getMaxBlockHeight() {
        byte[] mineOption = getConfigurationValue(ByteUtil.stringToUtf8Bytes(MINE_MAX_BLOCK_HEIGHT_KEY));
        if(mineOption == null){
            //设置默认值，这是一个十分巨大的数字，矿工永远挖不到的高度
            return Long.MAX_VALUE;
        }
        return Long.parseLong(ByteUtil.utf8BytesToString(mineOption));
    }

    @Override
    public long getMinerMineTimeInterval() {
        return MINE_TIMESTAMP_PER_ROUND;
    }



    private String getConfigurationDatabasePath(){
        return FileUtil.newPath(corePath, CONFIGURATION_DATABASE_NAME);
    }
    private byte[] getConfigurationValue(byte[] configurationKey) {
        return KvDbUtil.get(getConfigurationDatabasePath(), configurationKey);
    }
    private void addOrUpdateConfiguration(byte[] configurationKey, byte[] configurationValue) {
        KvDbUtil.put(getConfigurationDatabasePath(), configurationKey, configurationValue);
    }
}
