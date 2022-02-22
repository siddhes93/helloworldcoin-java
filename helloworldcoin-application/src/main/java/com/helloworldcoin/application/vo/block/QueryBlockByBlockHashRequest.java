package com.helloworldcoin.application.vo.block;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryBlockByBlockHashRequest {

    private String blockHash;




    //region get set
    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
    //endregion
}
