package com.helloworldcoin.application.vo.block;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class DeleteBlocksRequest {

    /**
     * The height of the removed block. Because blocks are continuous, blocks that height greater than or equal to this block height will be deleted
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
