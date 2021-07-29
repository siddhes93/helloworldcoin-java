package com.xingkaichun.helloworldblockchain.netcore.dto;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class GetNodesResponse {

    private List<NodeDto> nodes;

    public List<NodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }
}
