package com.helloworldcoin.application.vo.transaction;

import com.helloworldcoin.netcore.dto.TransactionDto;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class SubmitTransactionToBlockchainNetworkResponse {

    //交易
    private TransactionDto transaction;
    //交易成功提交的节点
    private List<String> successSubmitNodes;
    //交易提交失败的节点
    private List<String> failedSubmitNodes;




    //region get set
    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }

    public List<String> getSuccessSubmitNodes() {
        return successSubmitNodes;
    }

    public void setSuccessSubmitNodes(List<String> successSubmitNodes) {
        this.successSubmitNodes = successSubmitNodes;
    }

    public List<String> getFailedSubmitNodes() {
        return failedSubmitNodes;
    }

    public void setFailedSubmitNodes(List<String> failedSubmitNodes) {
        this.failedSubmitNodes = failedSubmitNodes;
    }
    //endregion
}
