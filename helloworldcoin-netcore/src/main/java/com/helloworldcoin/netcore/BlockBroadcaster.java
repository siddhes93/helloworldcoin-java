package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.Model2DtoTool;
import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.PostBlockRequest;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;

/**
 * block broadcaster : broadcast it's latest block to the whole network.
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockBroadcaster {

    private NetCoreConfiguration netCoreConfiguration;
    private BlockchainCore blockchainCore;
    private NodeService nodeService;

    public BlockBroadcaster(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastBlock();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockBroadcastTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'broadcast it's latest block to the whole network' error.",e);
        }
    }

    private void broadcastBlock() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            Block block = blockchainCore.queryTailBlock();
            if(block == null){
                return;
            }
            if(block.getHeight() <= node.getBlockchainHeight()){
                continue;
            }
            BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PostBlockRequest postBlockRequest = new PostBlockRequest();
            postBlockRequest.setBlock(blockDto);
            nodeClient.postBlock(postBlockRequest);
        }
    }

}
