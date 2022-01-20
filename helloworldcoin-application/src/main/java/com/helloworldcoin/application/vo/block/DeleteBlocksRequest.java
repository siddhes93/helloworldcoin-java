package com.helloworldcoin.application.vo.block;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class DeleteBlocksRequest {

    /**
     * 删除区块的高度。因为区块是连续的，所以大于等于这个高度的区块都将被删除
     */
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
