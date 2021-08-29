package com.xingkaichun.helloworldblockchain.application.vo.synchronizer;

/**
 *
 * @author sevenshi seven_shi@qq.com
 */
public class SetBlockHeightRequest {

    private long MaxBlockHeight;




    //region get set

    public long getMaxBlockHeight() {
        return blockHeight;
    }

    public void setMaxBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }


    //endregion
}
