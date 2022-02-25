package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.netcore.dto.PingRequest;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;


/**
 * Node Broadcaster: broadcasts itself to the whole network.
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeBroadcaster {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeBroadcaster(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastNode();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeBroadcastTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'broadcasts itself to the whole network' error.",e);
        }
    }

    private void broadcastNode() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            nodeClient.pingNode(pingRequest);
        }
    }

}
