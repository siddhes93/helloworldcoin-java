package com.helloworldcoin.netcore.dto;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class GetBlockResponse {

    private BlockDto block;




    //region get set
    public BlockDto getBlock() {
        return block;
    }

    public void setBlock(BlockDto block) {
        this.block = block;
    }
    //endregion
}
