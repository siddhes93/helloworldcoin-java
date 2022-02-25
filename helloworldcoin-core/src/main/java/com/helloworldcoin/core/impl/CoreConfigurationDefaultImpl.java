package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.CoreConfiguration;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.KvDbUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class CoreConfigurationDefaultImpl extends CoreConfiguration {

    private static final String CONFIGURATION_DATABASE_NAME = "ConfigurationDatabase";

    private static final String MINE_OPTION_KEY = "IS_MINER_ACTIVE";

    private static final String MINER_MINE_MAX_BLOCK_HEIGHT_KEY = "MINER_MINE_MAX_BLOCK_HEIGHT";



    private static final boolean MINE_OPTION_DEFAULT_VALUE = false;

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
