package com.helloworldcoin.netcore;

import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.server.NodeServer;
import com.helloworldcoin.netcore.service.NodeService;

/**
 * BlockchainNetCore
 *
 * @author x.king xdotking@gmail.com
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
        new Thread(()->blockchainCore.start()).start();
        new Thread(()->nodeServer.start()).start();

        new Thread(()->seedNodeInitializer.start()).start();
        new Thread(()->nodeBroadcaster.start()).start();
        new Thread(()->nodeSearcher.start()).start();
        new Thread(()->nodeCleaner.start()).start();

        new Thread(()->blockchainHeightBroadcaster.start()).start();
        new Thread(()->blockchainHeightSearcher.start()).start();

        new Thread(()->blockBroadcaster.start()).start();
        new Thread(()->blockSearcher.start()).start();

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
