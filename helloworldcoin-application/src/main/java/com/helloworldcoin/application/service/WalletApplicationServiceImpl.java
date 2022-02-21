package com.helloworldcoin.application.service;

import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionRequest;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionResponse;
import com.helloworldcoin.application.vo.wallet.PayeeVo;
import com.helloworldcoin.application.vo.wallet.PayerVo;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionRequest;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionResponse;
import com.helloworldcoin.core.model.wallet.Payee;
import com.helloworldcoin.core.model.wallet.Payer;
import com.helloworldcoin.netcore.BlockchainNetCore;
import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.dto.PostTransactionRequest;
import com.helloworldcoin.netcore.dto.PostTransactionResponse;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.netcore.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
@Service
public class WalletApplicationServiceImpl implements WalletApplicationService {

    @Autowired
    private BlockchainNetCore blockchainNetCore;



    @Override
    public SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request) {
        TransactionDto transactionDto = request.getTransaction();
        //post to local blockchain
        blockchainNetCore.getBlockchainCore().postTransaction(transactionDto);
        //post to other blockchain
        List<Node> nodes = blockchainNetCore.getNodeService().queryAllNodes();
        List<String> successSubmitNodes = new ArrayList<>();
        List<String> failedSubmitNodes = new ArrayList<>();
        if(nodes != null){
            for(Node node:nodes){
                PostTransactionRequest postTransactionRequest = new PostTransactionRequest();
                postTransactionRequest.setTransaction(transactionDto);
                NodeClient nodeClient = new NodeClientImpl(node.getIp());
                PostTransactionResponse postTransactionResponse = nodeClient.postTransaction(postTransactionRequest);
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

    @Override
    public AutomaticBuildTransactionResponse automaticBuildTransaction(AutomaticBuildTransactionRequest request) {
        AutoBuildTransactionRequest autoBuildTransactionRequest = new AutoBuildTransactionRequest();
        autoBuildTransactionRequest.setNonChangePayees(payeeVos2payees(request.getNonChangePayees()));

        AutoBuildTransactionResponse autoBuildTransactionResponse = blockchainNetCore.getBlockchainCore().autoBuildTransaction(autoBuildTransactionRequest);

        AutomaticBuildTransactionResponse response = new AutomaticBuildTransactionResponse();
        response.setBuildTransactionSuccess(autoBuildTransactionResponse.isBuildTransactionSuccess());
        response.setTransactionHash(autoBuildTransactionResponse.getTransactionHash());
        response.setFee(autoBuildTransactionResponse.getFee());
        response.setTransaction(autoBuildTransactionResponse.getTransaction());
        response.setPayers(payers2payerVos(autoBuildTransactionResponse.getPayers()));
        response.setPayees(payees2payeeVos(autoBuildTransactionResponse.getPayees()));
        response.setChangePayee(payee2payeeVo(autoBuildTransactionResponse.getChangePayee()));
        response.setNonChangePayees(payees2payeeVos(autoBuildTransactionResponse.getNonChangePayees()));

        return response;
    }

    private List<Payee> payeeVos2payees(List<PayeeVo> payeeVos){
        List<Payee> payees = new ArrayList<>();
        if(payeeVos != null){
            for (PayeeVo payeeVo:payeeVos) {
                Payee payee = payeeVo2payee(payeeVo);
                payees.add(payee);
            }
        }
        return payees;
    }
    private Payee payeeVo2payee(PayeeVo payeeVo){
        Payee payee = new Payee();
        payee.setAddress(payeeVo.getAddress());
        payee.setValue(payeeVo.getValue());
        return payee;
    }

    private List<PayerVo> payers2payerVos(List<Payer> payers){
        List<PayerVo> payerVos = new ArrayList<>();
        if(payers != null){
            for (Payer payer:payers) {
                PayerVo payerVo = payer2payerVo(payer);
                payerVos.add(payerVo);
            }
        }
        return payerVos;
    }
    private PayerVo payer2payerVo(Payer payer){
        PayerVo payerVo = new PayerVo();
        payerVo.setAddress(payer.getAddress());
        payerVo.setPrivateKey(payer.getPrivateKey());
        payerVo.setTransactionHash(payer.getTransactionHash());
        payerVo.setTransactionOutputIndex(payer.getTransactionOutputIndex());
        payerVo.setValue(payer.getValue());
        return payerVo;
    }
    private List<PayeeVo> payees2payeeVos(List<Payee> payees){
        List<PayeeVo> payeeVos = new ArrayList<>();
        if(payees != null){
            for (Payee payee:payees) {
                PayeeVo payeeVo = payee2payeeVo(payee);
                payeeVos.add(payeeVo);
            }
        }
        return payeeVos;
    }
    private PayeeVo payee2payeeVo(Payee payee){
        PayeeVo payeeVo = new PayeeVo();
        payeeVo.setAddress(payee.getAddress());
        payeeVo.setValue(payee.getValue());
        return payeeVo;
    }
}
