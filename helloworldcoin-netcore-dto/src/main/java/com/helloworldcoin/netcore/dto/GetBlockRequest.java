package com.helloworldcoin.netcore.dto;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class GetBlockRequest {

    private long blockHeight;




    //region get set
    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }
    //endregion
}
