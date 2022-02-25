package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.setting.NetworkSetting;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;


/**
 * Seed Node Initializer
 *
 * @author x.king xdotking@gmail.com
 */
public class SeedNodeInitializer {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public SeedNodeInitializer(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                addSeedNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getSeedNodeInitializeTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'add seed nodes' error.",e);
        }
    }

    private void addSeedNodes() {
        if(!netCoreConfiguration.isAutoSearchNode()){
            return;
        }

        for(String seedNode: NetworkSetting.SEED_NODES){
            Node node = new Node();
            node.setIp(seedNode);
            node.setBlockchainHeight(0);
            nodeService.addNode(node);
        }
    }

}
