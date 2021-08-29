package com.xingkaichun.helloworldblockchain.application.vo.block;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTop10BlocksResponse {

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
