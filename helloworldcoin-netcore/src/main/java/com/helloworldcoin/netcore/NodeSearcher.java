package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;


/**
 * 节点搜索器：在区块链网络中搜寻新的节点。
 *
 * @author x.king xdotking@gmail.com
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
            while (true){
                searchNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中搜索新的节点出现异常",e);
        }
    }

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
            handleGetNodesResponse(getNodesResponse);
        }
    }

    private void handleGetNodesResponse(GetNodesResponse getNodesResponse) {
        if(getNodesResponse == null){
            return;
        }
        List<NodeDto> nodes = getNodesResponse.getNodes();
        if(nodes == null || nodes.size()==0){
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
                n.setBlockchainHeight(0);
                nodeService.addNode(n);
                LogUtil.debug("自动机制发现节点["+node.getIp()+"]，已在节点数据库中添加了该节点。");
            }
        }
    }

}
