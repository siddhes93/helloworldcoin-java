package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.configuration.NetCoreConfigurationImpl;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.netcore.service.NodeServiceImpl;
import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.core.BlockchainCoreFactory;
import com.helloworldcoin.core.tool.ResourcePathTool;
import com.helloworldcoin.netcore.dao.NodeDao;
import com.helloworldcoin.netcore.dao.impl.NodeDaoImpl;
import com.helloworldcoin.netcore.server.NodeServer;
import com.helloworldcoin.util.FileUtil;

/**
 * 区块链网络版核心工厂
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainNetCoreFactory {

    /**
     * 创建[区块链网络版核心]实例
     */
    public static BlockchainNetCore createDefaultBlockchainNetCore(){
        return createBlockchainNetCore(ResourcePathTool.getDataRootPath());
    }

    /**
     * 创建[区块链网络版核心]实例
     *
     * @param netcorePath 区块链数据存放位置
     */
    public static BlockchainNetCore createBlockchainNetCore(String netcorePath){
        NetCoreConfiguration netCoreConfiguration = new NetCoreConfigurationImpl(netcorePath);

        String blockchainCorePath = FileUtil.newPath(netcorePath,"BlockchainCore");
        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(blockchainCorePath);
        String slaveBlockchainCorePath = FileUtil.newPath(netcorePath,"SlaveBlockchainCore");
        BlockchainCore slaveBlockchainCore = BlockchainCoreFactory.createBlockchainCore(slaveBlockchainCorePath);


        NodeDao nodeDao = new NodeDaoImpl(netCoreConfiguration);
        NodeService nodeService = new NodeServiceImpl(nodeDao);
        NodeServer nodeServer = new NodeServer(netCoreConfiguration,blockchainCore,nodeService);

        SeedNodeInitializer seedNodeInitializer = new SeedNodeInitializer(netCoreConfiguration,nodeService);
        NodeSearcher nodeSearcher = new NodeSearcher(netCoreConfiguration,nodeService);
        NodeBroadcaster nodeBroadcaster = new NodeBroadcaster(netCoreConfiguration,nodeService);
        NodeCleaner nodeCleaner = new NodeCleaner(netCoreConfiguration,nodeService);

        BlockchainHeightSearcher blockchainHeightSearcher = new BlockchainHeightSearcher(netCoreConfiguration,nodeService);
        BlockchainHeightBroadcaster blockchainHeightBroadcaster = new BlockchainHeightBroadcaster(netCoreConfiguration,blockchainCore,nodeService);

        BlockSearcher blockSearcher = new BlockSearcher(netCoreConfiguration,blockchainCore,slaveBlockchainCore,nodeService);
        BlockBroadcaster blockBroadcaster = new BlockBroadcaster(netCoreConfiguration,blockchainCore,nodeService);

        UnconfirmedTransactionsSearcher unconfirmedTransactionsSearcher = new UnconfirmedTransactionsSearcher(netCoreConfiguration,blockchainCore,nodeService);

        BlockchainNetCore blockchainNetCore
                = new BlockchainNetCore(netCoreConfiguration, blockchainCore, nodeServer, nodeService
                , seedNodeInitializer, nodeSearcher, nodeBroadcaster, nodeCleaner
                , blockchainHeightSearcher, blockchainHeightBroadcaster
                , blockSearcher, blockBroadcaster
                , unconfirmedTransactionsSearcher
        );
        return blockchainNetCore;
    }

}
