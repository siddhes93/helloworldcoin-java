package com.xingkaichun.helloworldblockchain.netcore.model;


/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockchainForkBlockEntity {

    private long blockHeight;
    private String blockHash;




    //region get set

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    //endregion
}
