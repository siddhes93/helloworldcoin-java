package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.CoreConfiguration;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.KvDbUtil;

/**
 * 默认实现
 *
 * @author x.king xdotking@gmail.com
 */
public class CoreConfigurationDefaultImpl extends CoreConfiguration {

    //配置数据库名字
    private static final String CONFIGURATION_DATABASE_NAME = "ConfigurationDatabase";

    //'矿工是否是激活状态'存入到数据库时的主键
    private static final String MINE_OPTION_KEY = "IS_MINER_ACTIVE";

    //'矿工可挖的最高区块高度'存入到数据库时的主键
    private static final String MINER_MINE_MAX_BLOCK_HEIGHT_KEY = "MINER_MINE_MAX_BLOCK_HEIGHT";



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
        return ByteUtil.utf8BytesToBoolean(mineOption);
    }

    @Override
    public void activeMiner() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(MINE_OPTION_KEY),ByteUtil.booleanToUtf8Bytes(true));
    }

    @Override
    public void deactiveMiner() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(MINE_OPTION_KEY),ByteUtil.booleanToUtf8Bytes(false));
    }

    @Override
    public void setMinerMineMaxBlockHeight(long maxHeight) {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(MINER_MINE_MAX_BLOCK_HEIGHT_KEY),ByteUtil.uint64ToBytes(maxHeight));
    }

    @Override
    public long getMinerMineMaxBlockHeight() {
        byte[] bytesMineMaxBlockHeight = getConfigurationValue(ByteUtil.stringToUtf8Bytes(MINER_MINE_MAX_BLOCK_HEIGHT_KEY));
        if(bytesMineMaxBlockHeight == null){
            //设置默认值，这是一个十分巨大的数字，矿工永远挖不到的高度
            return 10000000000000000L;
        }
        return ByteUtil.bytesToUint64(bytesMineMaxBlockHeight);
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
