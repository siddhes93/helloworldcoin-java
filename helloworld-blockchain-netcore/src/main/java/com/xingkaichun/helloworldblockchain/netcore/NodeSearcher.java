package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;


/**
 * 节点搜索器：在区块链网络中搜寻新的节点。
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeSearcher {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeSearcher(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while(true){
                searchNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getSearchNodeTimeInterval());
            }
        } catch (Exception e) {
            SystemUtil.errorExit("在区块链网络中搜索新的节点出现异常",e);
        }
    }

    /**
     * 在区块链网络中搜寻新的节点
     */
    private void searchNodes() {
        if(!netCoreConfiguration.isAutoSearchNode()){
            return;
        }
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            if(!netCoreConfiguration.isAutoSearchNode()){
                return;
            }
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            GetNodesRequest getNodesRequest = new GetNodesRequest();
            GetNodesResponse getNodesResponse = nodeClient.getNodes(getNodesRequest);
            resolveGetNodesResponse(getNodesResponse);
        }
    }

    private void resolveGetNodesResponse(GetNodesResponse getNodesResponse) {
        if(getNodesResponse == null){
            return;
        }
        List<NodeDto> nodes = getNodesResponse.getNodes();
        if(nodes == null){
            return;
        }

        for(NodeDto node:nodes){
            if(!netCoreConfiguration.isAutoSearchNode()){
                return;
            }
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest);
            if(pingResponse != null){
                Node n = new Node();
                n.setIp(node.getIp());
                nodeService.addNode(n);
                LogUtil.debug("自动机制发现节点["+node+"]，节点已加入节点数据库。");
            }
        }
    }

}
