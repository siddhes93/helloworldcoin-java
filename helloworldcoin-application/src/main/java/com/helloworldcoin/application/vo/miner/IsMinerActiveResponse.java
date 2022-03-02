package com.helloworldcoin.application.vo.miner;

/**
 *
 * @author x.king xdotking@gmail.com
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
