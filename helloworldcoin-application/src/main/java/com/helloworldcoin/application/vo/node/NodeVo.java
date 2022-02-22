package com.helloworldcoin.application.vo.node;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeVo {
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
