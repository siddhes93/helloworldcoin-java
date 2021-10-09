package com.xingkaichun.helloworldblockchain.application.service;

import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.xingkaichun.helloworldblockchain.application.vo.wallet.*;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Payee;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Payer;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostTransactionRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostTransactionResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
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



    @Override
    public SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request) {
        TransactionDto transactionDto = request.getTransaction();
        //将交易提交到本地区块链
        blockchainNetCore.getBlockchainCore().postTransaction(transactionDto);
        //提交交易到网络
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
        response.setMessage(payAlert2PayAlertVo(autoBuildTransactionResponse.getMessage()));
        response.setTransactionHash(autoBuildTransactionResponse.getTransactionHash());
        response.setFee(autoBuildTransactionResponse.getFee());
        response.setTransaction(autoBuildTransactionResponse.getTransaction());
        response.setPayers(payers2payerVos(autoBuildTransactionResponse.getPayers()));
        response.setPayees(payees2payeeVos(autoBuildTransactionResponse.getPayees()));
        response.setChangePayee(payee2payeeVo(autoBuildTransactionResponse.getChangePayee()));
        response.setNonChangePayees(payees2payeeVos(autoBuildTransactionResponse.getNonChangePayees()));

        return response;
    }

    private String payAlert2PayAlertVo(String message) {
        if(StringUtil.isEmpty(message)){
            return message;
        }
        if(StringUtil.equals(PayAlertVo.PAYEE_CAN_NOT_EMPTY,message)){
            return PayAlertVo.PAYEE_CAN_NOT_EMPTY;
        }else if(StringUtil.equals(PayAlertVo.PAYEE_VALUE_CAN_NOT_LESS_EQUAL_THAN_ZERO,message)){
            return PayAlertVo.PAYEE_VALUE_CAN_NOT_LESS_EQUAL_THAN_ZERO;
        }else if(StringUtil.equals(PayAlertVo.NOT_ENOUGH_MONEY_TO_PAY,message)){
            return PayAlertVo.NOT_ENOUGH_MONEY_TO_PAY;
        }else if(StringUtil.equals(PayAlertVo.PAYEE_ADDRESS_CAN_NOT_EMPTY,message)){
            return PayAlertVo.PAYEE_ADDRESS_CAN_NOT_EMPTY;
        }else {
            throw new RuntimeException();
        }
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
