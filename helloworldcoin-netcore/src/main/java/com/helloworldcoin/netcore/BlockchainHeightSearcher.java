package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.netcore.dto.GetBlockchainHeightRequest;
import com.helloworldcoin.netcore.dto.GetBlockchainHeightResponse;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;


/**
 * Blockchain Height Searcher : search for node‘s Blockchain Height in the blockchain network.
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainHeightSearcher {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public BlockchainHeightSearcher(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                searchBlockchainHeight();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockchainHeightSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'search for node‘s Blockchain Height in the blockchain network' error.",e);
        }
    }

    private void searchBlockchainHeight() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            GetBlockchainHeightRequest getBlockchainHeightRequest = new GetBlockchainHeightRequest();
            GetBlockchainHeightResponse getBlockchainHeightResponse = nodeClient.getBlockchainHeight(getBlockchainHeightRequest);
            if(getBlockchainHeightResponse != null){
                node.setBlockchainHeight(getBlockchainHeightResponse.getBlockchainHeight());
                nodeService.updateNode(node);
            }
        }
    }

}
