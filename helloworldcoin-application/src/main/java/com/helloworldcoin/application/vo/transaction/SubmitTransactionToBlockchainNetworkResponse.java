package com.helloworldcoin.application.vo.transaction;

import com.helloworldcoin.netcore.dto.TransactionDto;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class SubmitTransactionToBlockchainNetworkResponse {

    //transaction
    private TransactionDto transaction;
    //successfully submitted nodes
    private List<String> successSubmitNodes;
    //Commit failed node
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
