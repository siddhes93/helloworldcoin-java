package com.xingkaichun.helloworldblockchain.application.vo.synchronizer;

import com.xingkaichun.helloworldblockchain.application.vo.block.BlockVo;

/**
 *
 * @author sevenshi seven_shi@qq.com
 */
public class SetMaxBlockHeightResponse {
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
