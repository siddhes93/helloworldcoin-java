package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.dto.PostBlockchainHeightRequest;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;

/**
 * blockchain height broadcaster : broadcast the blockchain height to the whole network.
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainHeightBroadcaster {

    private NetCoreConfiguration netCoreConfiguration;
    private BlockchainCore blockchainCore;
    private NodeService nodeService;

    public BlockchainHeightBroadcaster(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastBlockchainHeight();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockchainHeightBroadcastTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'broadcast the blockchain height to the whole network' error.",e);
        }
    }

    private void broadcastBlockchainHeight() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            long blockchainHeight = blockchainCore.queryBlockchainHeight();
            if(blockchainHeight <= node.getBlockchainHeight()){
                continue;
            }
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PostBlockchainHeightRequest postBlockchainHeightRequest = new PostBlockchainHeightRequest();
            postBlockchainHeightRequest.setBlockchainHeight(blockchainHeight);
            nodeClient.postBlockchainHeight(postBlockchainHeightRequest);
        }
    }

}
