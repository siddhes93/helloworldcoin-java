package com.xingkaichun.helloworldblockchain.application.vo.node;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryAllNodesResponse {

    private List<NodeVo> nodes;




    //region get set
    public List<NodeVo> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeVo> nodes) {
        this.nodes = nodes;
    }
    //endregion
}
