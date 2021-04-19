package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.server.BlockchainNodeHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;

/**
 * 网络版区块链核心，代表一个完整的网络版区块链核心系统。
 * 网络版区块链核心系统，由以下几部分组成：
 * 1.单机版[没有网络交互版本]区块链核心
 * @see com.xingkaichun.helloworldblockchain.core.BlockchainCore
 * 2.节点搜寻器
 * @see com.xingkaichun.helloworldblockchain.netcore.NodeSearcher
 * 3.节点广播者
 * @see com.xingkaichun.helloworldblockchain.netcore.NodeBroadcaster
 * 4.区块搜寻器
 * @see com.xingkaichun.helloworldblockchain.netcore.BlockSearcher
 * 5.区块广播者
 * @see com.xingkaichun.helloworldblockchain.netcore.BlockBroadcaster
 *
 * @author 邢开春 409060350@qq.com
 */
public class NetBlockchainCore {

    private BlockchainCore blockchainCore;
    private BlockchainNodeHttpServer blockchainNodeHttpServer;
    private NodeSearcher nodeSearcher;
    private NodeBroadcaster nodeBroadcaster;
    private BlockSearcher blockSearcher;
    private BlockBroadcaster blockBroadcaster;

    private ConfigurationService configurationService;
    private NodeService nodeService;

    public NetBlockchainCore(BlockchainCore blockchainCore
            , BlockchainNodeHttpServer blockchainNodeHttpServer, ConfigurationService configurationService
            , NodeSearcher nodeSearcher, NodeBroadcaster nodeBroadcaster
            , BlockSearcher blockSearcher , BlockBroadcaster blockBroadcaster
            , NodeService nodeService) {

        this.blockchainCore = blockchainCore;
        this.blockchainNodeHttpServer = blockchainNodeHttpServer;
        this.configurationService = configurationService;
        this.nodeSearcher = nodeSearcher;
        this.nodeBroadcaster = nodeBroadcaster;
        this.blockSearcher = blockSearcher;
        this.blockBroadcaster = blockBroadcaster;

        this.nodeService = nodeService;
        restoreConfiguration();
    }

    /**
     * 恢复配置
     */
    private void restoreConfiguration() {
        //恢复矿工配置
        configurationService.restoreMinerConfiguration();
    }

    /**
     * 删除区块高度大于等于@blockHeight@的区块
     */
    public void deleteBlocks(long blockHeight) {
        blockchainCore.deleteBlocks(blockHeight);
        //TODO blockSearcher本身结构问题，导致必须调用该对象对的删除区块方法。考虑删除该方法。
        blockSearcher.deleteBlocks(blockHeight);
    }

    public void start() {
        //启动本地的单机区块链
        blockchainCore.start();
        //启动区块链节点服务器
        blockchainNodeHttpServer.start();

        //启动节点搜寻器
        nodeSearcher.start();
        //启动节点广播器
        nodeBroadcaster.start();
        //启动区块搜寻器
        blockSearcher.start();
        //启动区块广播者
        blockBroadcaster.start();
    }

    //region get set
    public BlockchainCore getBlockchainCore() {
        return blockchainCore;
    }

    public BlockchainNodeHttpServer getBlockchainNodeHttpServer() {
        return blockchainNodeHttpServer;
    }

    public NodeSearcher getNodeSearcher() {
        return nodeSearcher;
    }

    public BlockSearcher getBlockSearcher() {
        return blockSearcher;
    }

    public BlockBroadcaster getBlockBroadcaster() {
        return blockBroadcaster;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public NodeBroadcaster getNodeBroadcaster() {
        return nodeBroadcaster;
    }

    public NodeService getNodeService() {
        return nodeService;
    }
    //end
}
