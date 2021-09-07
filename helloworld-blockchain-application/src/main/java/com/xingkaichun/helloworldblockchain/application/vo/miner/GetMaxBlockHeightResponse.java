package com.xingkaichun.helloworldblockchain.application.vo.miner;

/**
 *
 * @author sevenshi seven_shi@qq.com
 */
public class GetMaxBlockHeightResponse {

    private long maxBlockHeight;


    //region get set
    public long getMaxBlockHeight() {
        return maxBlockHeight;
    }

    public void setMaxBlockHeight(long maxBlockHeight) {
        this.maxBlockHeight = maxBlockHeight;
    }
    //endregion
}
