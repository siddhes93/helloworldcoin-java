package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tool.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.configuration.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostBlockRequest;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;

/**
 * 区块广播器：主动将自己最新的区块广播至全网。
 * 别的节点可能由于这样那样的原因，不来同步我的区块，可我的这个区块对我很重要
 * ，例如我在全网最先新挖出了一个区块，可没人来同步我
 * ，我的区块不能传播至全网，这意味着我白挖了一个区块(区块里有我的奖励，全网收不到这个区块，意味着我的奖励不被全网认可)
 * ，我心有不甘呀，我只有尝试将我的区块硬推给别的节点了
 * ，如果别的节点真的是由于有'这样那样的原因'没来同步我，而不是恶意不同步我
 * ，那它接到我硬推给它的区块，它肯定会将接收到的区块保存到自身区块链之中
 * ，最终我的区块将被全网接受，自然我的挖矿奖励也同时被全网认可了。
 *
 * @author 邢开春 409060350@qq.com
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
            LogUtil.error("在区块链网络中广播自己的区块出现异常",e);
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
