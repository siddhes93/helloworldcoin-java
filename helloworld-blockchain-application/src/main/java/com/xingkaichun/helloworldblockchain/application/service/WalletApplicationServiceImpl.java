package com.xingkaichun.helloworldblockchain.application.service;

import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostTransactionRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostTransactionResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
@Service
public class WalletApplicationServiceImpl implements WalletApplicationService {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private BlockchainCore blockchainCore;



    @Override
    public SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request) {
        TransactionDto transactionDto = request.getTransaction();
        //将交易提交到本地区块链
        blockchainCore.postTransaction(transactionDto);
        //提交交易到网络
        List<Node> nodes = blockchainNetCore.getNodeService().queryAllNodes();
        List<String> successSubmitNodes = new ArrayList<>();
        List<String> failedSubmitNodes = new ArrayList<>();
        if(nodes != null){
            for(Node node:nodes){
                PostTransactionRequest postTransactionRequest = new PostTransactionRequest();
                postTransactionRequest.setTransaction(transactionDto);
                PostTransactionResponse postTransactionResponse = new NodeClientImpl(node.getIp()).postTransaction(postTransactionRequest);
                if(postTransactionResponse != null){
                    successSubmitNodes.add(node.getIp());
                } else {
                    failedSubmitNodes.add(node.getIp());
                }
            }
        }

        SubmitTransactionToBlockchainNetworkResponse response = new SubmitTransactionToBlockchainNetworkResponse();
        response.setTransaction(transactionDto);
        response.setSuccessSubmitNodes(successSubmitNodes);
        response.setFailedSubmitNodes(failedSubmitNodes);
        return response;
    }
}
