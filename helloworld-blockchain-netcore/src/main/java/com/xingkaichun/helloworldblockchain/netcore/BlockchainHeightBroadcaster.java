package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.configuration.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostBlockchainHeightRequest;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;

/**
 * 区块链高度广播器：将区块链高度传播至全网。
 * 如果本地区块链的高度高于全网，那么就应该(通过在区块链网络中广播自己的高度的方式)通知其它节点
 * ，好让其它节点知道可以来同步自己的区块数据了。
 * 至于其它节点什么时候来同步自己的区块，应该由其它节点来决定。
 *
 * 顺便说一句，矿工把区块放入区块链后，当区块广播器广播区块链高度时，
 * 也就相当于通知了其它节点"自己挖出了新的区块"这件事。
 *
 * @author 邢开春 409060350@qq.com
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
            LogUtil.error("在区块链网络中广播自身区块链高度异常",e);
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
