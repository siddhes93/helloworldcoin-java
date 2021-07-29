package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.server.NodeServer;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;

/**
 * 区块链网络版核心，代表一个完整的区块链网络版核心系统，它就是所谓的节点。
 * 区块链网络版核心底层依赖单机版区块链核心BlockchainCore，在单机版区块链系统的基础上
 * ，新增了网络功能：自动的在整个区块链网络中寻找/发布：节点、区块、交易。
 *
 * 在启动时，它通过[种子节点初始化器]，将种子节点加入到自己已知的节点列表，
 * 通过[节点搜寻器]搜索区块链网络中的节点，
 * 通过[节点广播器]将自己的存在告诉其它节点，
 * 通过[区块链高度搜索器]搜索已知节点的高度，
 * 通过[区块搜寻器]查找最新的区块，
 * 通过[区块链高度广播器]将自己的高度告诉其它节点，
 * 通过[区块广播器]广播自己最新的区块，
 * 每个[区块链网络版核心]都会做出上述操作，从而相互之间互联起来，共同协作，构成了区块链网络。
 *
 * 区块链网络版核心系统，由以下几部分组成：
 * 单机版[没有网络交互版本]区块链核心
 * @see com.xingkaichun.helloworldblockchain.core.BlockchainCore
 * 种子节点初始化器
 * @see com.xingkaichun.helloworldblockchain.netcore.SeedNodeInitializer
 * 节点搜寻器
 * @see com.xingkaichun.helloworldblockchain.netcore.NodeSearcher
 * 节点广播器
 * @see com.xingkaichun.helloworldblockchain.netcore.NodeBroadcaster
 * 区块链高度搜索器
 * @see com.xingkaichun.helloworldblockchain.netcore.BlockchainHeightSearcher
 * 区块链高度广播器
 * @see com.xingkaichun.helloworldblockchain.netcore.BlockchainHeightBroadcaster
 * 区块搜寻器
 * @see com.xingkaichun.helloworldblockchain.netcore.BlockSearcher
 * 区块广播器
 * @see com.xingkaichun.helloworldblockchain.netcore.BlockBroadcaster
 * 未确认交易搜索器
 * @see com.xingkaichun.helloworldblockchain.netcore.UnconfirmedTransactionsSearcher
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainNetCore {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    private BlockchainCore blockchainCore;
    private NodeServer nodeServer;

    private SeedNodeInitializer seedNodeInitializer;
    private NodeSearcher nodeSearcher;
    private NodeBroadcaster nodeBroadcaster;
    private NodeCleaner nodeCleaner;

    private BlockchainHeightSearcher blockchainHeightSearcher;
    private BlockchainHeightBroadcaster blockchainHeightBroadcaster;

    private BlockSearcher blockSearcher;
    private BlockBroadcaster blockBroadcaster;

    private UnconfirmedTransactionsSearcher unconfirmedTransactionsSearcher;


    public BlockchainNetCore(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeServer nodeServer
            , NodeService nodeService
            , SeedNodeInitializer seedNodeInitializer, NodeSearcher nodeSearcher, NodeBroadcaster nodeBroadcaster, NodeCleaner nodeCleaner
            , BlockchainHeightSearcher blockchainHeightSearcher, BlockchainHeightBroadcaster blockchainHeightBroadcaster
            , BlockSearcher blockSearcher, BlockBroadcaster blockBroadcaster
            , UnconfirmedTransactionsSearcher unconfirmedTransactionsSearcher
        ) {
        this.netCoreConfiguration = netCoreConfiguration;

        this.blockchainCore = blockchainCore;
        this.nodeServer = nodeServer;
        this.nodeService = nodeService;
        this.nodeCleaner = nodeCleaner;

        this.seedNodeInitializer = seedNodeInitializer;
        this.nodeBroadcaster = nodeBroadcaster;
        this.nodeSearcher = nodeSearcher;

        this.blockchainHeightSearcher = blockchainHeightSearcher;
        this.blockchainHeightBroadcaster = blockchainHeightBroadcaster;

        this.blockBroadcaster = blockBroadcaster;
        this.blockSearcher = blockSearcher;

        this.unconfirmedTransactionsSearcher = unconfirmedTransactionsSearcher;
    }

    public void start() {
        //启动本地的单机区块链
        new Thread(()->blockchainCore.start()).start();
        //启动区块链节点服务器
        new Thread(()->nodeServer.start()).start();

        //种子节点初始化器
        new Thread(()->seedNodeInitializer.start()).start();
        //启动节点广播器
        new Thread(()->nodeBroadcaster.start()).start();
        //启动节点搜寻器
        new Thread(()->nodeSearcher.start()).start();
        //启动节点清理器
        new Thread(()->nodeCleaner.start()).start();

        //启动区块链高度广播器
        new Thread(()->blockchainHeightBroadcaster.start()).start();
        //启动区块链高度搜索器
        new Thread(()->blockchainHeightSearcher.start()).start();

        //启动区块广播器
        new Thread(()->blockBroadcaster.start()).start();
        //启动区块搜寻器
        new Thread(()->blockSearcher.start()).start();

        //未确认交易搜索器
        new Thread(()->unconfirmedTransactionsSearcher.start()).start();
    }

    //region get set
    public NetCoreConfiguration getNetCoreConfiguration() {
        return netCoreConfiguration;
    }
    public BlockchainCore getBlockchainCore() {
        return blockchainCore;
    }
    public NodeServer getNodeServer() {
        return nodeServer;
    }
    public NodeService getNodeService() {
        return nodeService;
    }
    public SeedNodeInitializer getSeedNodeInitializer() {
        return seedNodeInitializer;
    }
    public NodeSearcher getNodeSearcher() {
        return nodeSearcher;
    }
    public NodeBroadcaster getNodeBroadcaster() {
        return nodeBroadcaster;
    }
    public NodeCleaner getNodeCleaner() {
        return nodeCleaner;
    }
    public BlockchainHeightSearcher getBlockchainHeightSearcher() {
        return blockchainHeightSearcher;
    }
    public BlockchainHeightBroadcaster getBlockchainHeightBroadcaster() {
        return blockchainHeightBroadcaster;
    }
    public BlockSearcher getBlockSearcher() {
        return blockSearcher;
    }
    public BlockBroadcaster getBlockBroadcaster() {
        return blockBroadcaster;
    }
    public UnconfirmedTransactionsSearcher getUnconfirmedTransactionsSearcher() {
        return unconfirmedTransactionsSearcher;
    }
    //end
}
