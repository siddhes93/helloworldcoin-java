package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.dto.PingRequest;
import com.helloworldcoin.netcore.dto.PingResponse;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;


/**
 * node cleaner : clean up dead nodes
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeCleaner {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeCleaner(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                cleanDeadNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeCleanTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'clean up dead nodes' error.",e);
        }
    }

    private void cleanDeadNodes() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest);
            if(pingResponse == null){
                nodeService.deleteNode(node.getIp());
            }
        }
    }

}
