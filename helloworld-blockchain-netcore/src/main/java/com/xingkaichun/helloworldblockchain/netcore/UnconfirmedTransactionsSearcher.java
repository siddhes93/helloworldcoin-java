package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.GetUnconfirmedTransactionsRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.GetUnconfirmedTransactionsResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;

/**
 * 未确认交易搜索器
 * 搜索区块链网络中的未确认交易，放入未确认交易池，等待矿工用于挖矿。
 *
 * @author 邢开春 409060350@qq.com
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
                ThreadUtil.millisecondSleep(netCoreConfiguration.getSearchUnconfirmedTransactionsTimeInterval());
            }
        } catch (Exception e) {
            SystemUtil.errorExit("在区块链网络中搜寻未确认交易出现异常",e);
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
                try {
                    blockchainCore.getUnconfirmedTransactionDatabase().insertTransaction(transaction);
                }catch (Exception e){
                    LogUtil.error("交易["+JsonUtil.toString(transaction)+"]放入交易池异常。",e);
                }
            }
        }
    }

}
