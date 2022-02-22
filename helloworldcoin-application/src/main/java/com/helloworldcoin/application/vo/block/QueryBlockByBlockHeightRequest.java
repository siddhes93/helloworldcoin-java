package com.helloworldcoin.application.vo.block;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryBlockByBlockHeightRequest {

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
