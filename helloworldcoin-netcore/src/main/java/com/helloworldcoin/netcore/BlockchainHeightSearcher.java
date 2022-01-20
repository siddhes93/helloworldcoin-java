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
 * 区块链高度搜索器
 * 为什么要搜索节点的高度？
 * 在我的设计之中，本节点已知的所有节点的信息(ip、区块链高度等)都持久化在本地，区块链高度搜索器定时的更新已知节点的高度。
 * 区块搜寻器BlockSearcher定时的用本地区块链高度与已知节点的区块链高度(存储在本地的高度)作比较
 * ，若本地区块链高度较小，本地区块链则去同步远程节点的区块。
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
            LogUtil.error("在区块链网络中搜索节点的高度异常",e);
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
