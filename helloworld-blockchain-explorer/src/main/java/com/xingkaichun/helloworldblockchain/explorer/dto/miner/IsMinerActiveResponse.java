package com.xingkaichun.helloworldblockchain.explorer.dto.miner;

/**
 *
 * @author 邢开春
 */
public class IsMinerActiveResponse {

    private boolean minerInActiveState;




    //region get set

    public boolean isMinerInActiveState() {
        return minerInActiveState;
    }

    public void setMinerInActiveState(boolean minerInActiveState) {
        this.minerInActiveState = minerInActiveState;
    }


    //endregion
}
