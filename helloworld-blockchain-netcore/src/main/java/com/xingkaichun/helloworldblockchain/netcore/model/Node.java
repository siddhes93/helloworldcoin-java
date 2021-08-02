package com.xingkaichun.helloworldblockchain.netcore.model;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class Node {
    private String ip;
    private long blockchainHeight;


    //region get set
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public long getBlockchainHeight() {
        return blockchainHeight;
    }
    public void setBlockchainHeight(long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }
    //endregion
}
