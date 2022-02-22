package com.helloworldcoin.application.vo.block;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryLatest10BlocksResponse {

    private List<BlockVo2> blocks;




    //region get set
    public List<BlockVo2> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockVo2> blocks) {
        this.blocks = blocks;
    }
    //endregion
}
