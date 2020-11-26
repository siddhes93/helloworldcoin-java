package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainCoreFactory;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.ConfigurationDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.NodeDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.node.server.BlockchainNodeHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.node.server.HttpServerHandlerResolver;
import com.xingkaichun.helloworldblockchain.netcore.service.*;
import com.xingkaichun.helloworldblockchain.util.FileUtil;

/**
 * 网络版区块链核心工厂
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class NetBlockchainCoreFactory {

    /**
     * 创建NetBlockchainCore实例
     */
    public static NetBlockchainCore createNetBlockchainCore(){
        return createNetBlockchainCore(ResourcePathTool.getDataRootPath());
    }

    /**
     * 创建NetBlockchainCore实例
     *
     * @param dataRootPath 区块链数据存放位置
     */
    public static NetBlockchainCore createNetBlockchainCore(String dataRootPath){
        if(dataRootPath == null){
            throw new NullPointerException("参数路径不能为空。");
        }
        FileUtil.mkdir(dataRootPath);

        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(dataRootPath);
        BlockchainCore slaveBlockchainCore = BlockchainCoreFactory.createBlockchainCore(dataRootPath+"/slave");

        ConfigurationDao configurationDao = new ConfigurationDaoImpl(dataRootPath);
        ConfigurationService configurationService = new ConfigurationServiceImpl(blockchainCore,configurationDao);

        NodeDao nodeDao = new NodeDaoImpl(dataRootPath);
        NodeService nodeService = new NodeServiceImpl(nodeDao);
        BlockchainNodeClient blockchainNodeClient = new BlockchainNodeClientImpl();

        SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService = new SynchronizeRemoteNodeBlockServiceImpl(blockchainCore,slaveBlockchainCore,nodeService, blockchainNodeClient,configurationService);

        HttpServerHandlerResolver httpServerHandlerResolver = new HttpServerHandlerResolver(blockchainCore,nodeService,configurationService);
        BlockchainNodeHttpServer blockchainNodeHttpServer = new BlockchainNodeHttpServer(httpServerHandlerResolver);
        NodeSearcher nodeSearcher = new NodeSearcher(configurationService,nodeService, blockchainNodeClient);
        NodeBroadcaster nodeBroadcaster = new NodeBroadcaster(nodeService, blockchainNodeClient);
        BlockSearcher blockSearcher = new BlockSearcher(nodeService,synchronizeRemoteNodeBlockService,blockchainCore, slaveBlockchainCore, blockchainNodeClient);
        BlockBroadcaster blockBroadcaster = new BlockBroadcaster(nodeService,blockchainCore, blockchainNodeClient);
        NetBlockchainCore netBlockchainCore
                = new NetBlockchainCore(blockchainCore, slaveBlockchainCore, blockchainNodeHttpServer, configurationService
                ,nodeSearcher,nodeBroadcaster,blockSearcher, blockBroadcaster
                ,nodeService, blockchainNodeClient);
        return netBlockchainCore;
    }
}
