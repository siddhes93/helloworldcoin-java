package com.helloworldcoin.netcore.dto;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class GetNodesResponse {

    private List<NodeDto> nodes;




    //region get set
    public List<NodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }
    //endregion
}
