package com.xingkaichun.helloworldblockchain.explorer.dto.block;

/**
 *
 * @author 邢开春
 */
public class DeleteBlockRequest {

    /**
     * 删除区块的高度。因为区块是连续的，所以大于等于这个高度的区块都将被删除
     */
    private Long blockHeight;




    //region get set

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    //endregion
}
