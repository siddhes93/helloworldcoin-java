package com.helloworldcoin.application.vo.node;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
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
