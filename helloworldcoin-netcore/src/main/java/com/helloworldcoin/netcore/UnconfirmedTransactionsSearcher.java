package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.dto.GetUnconfirmedTransactionsRequest;
import com.helloworldcoin.netcore.dto.GetUnconfirmedTransactionsResponse;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;

/**
 * 未确认交易搜索器
 * 搜索区块链网络中的未确认交易，放入未确认交易池，等待矿工用于挖矿。
 *
 * @author x.king xdotking@gmail.com
 */
public class UnconfirmedTransactionsSearcher {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;


    public UnconfirmedTransactionsSearcher(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
    }

    public void start() {
        try {
            while (true){
                searchUnconfirmedTransactions();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getUnconfirmedTransactionsSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中搜寻未确认交易出现异常",e);
        }
    }

    private void searchUnconfirmedTransactions() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            GetUnconfirmedTransactionsRequest getUnconfirmedTransactionsRequest = new GetUnconfirmedTransactionsRequest();
            GetUnconfirmedTransactionsResponse getUnconfirmedTransactionsResponse = nodeClient.getUnconfirmedTransactions(getUnconfirmedTransactionsRequest);
            if(getUnconfirmedTransactionsResponse == null){
                continue;
            }
            List<TransactionDto> transactions = getUnconfirmedTransactionsResponse.getTransactions();
            if(transactions == null){
                continue;
            }
            for(TransactionDto transaction:transactions){
                blockchainCore.getUnconfirmedTransactionDatabase().insertTransaction(transaction);
            }
        }
    }

}
