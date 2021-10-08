package com.xingkaichun.helloworldblockchain.application.vo.block;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockVo2 {

    private long height;
    private long blockSize;
    private long transactionCount;
    private long minerIncentiveValue;
    private String time;
    private String hash;

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public long getMinerIncentiveValue() {
        return minerIncentiveValue;
    }

    public void setMinerIncentiveValue(long minerIncentiveValue) {
        this.minerIncentiveValue = minerIncentiveValue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
