package com.helloworldcoin.application.vo.wallet;

import com.helloworldcoin.netcore.dto.TransactionDto;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class AutomaticBuildTransactionResponse {

    private boolean buildTransactionSuccess;

    private String transactionHash;
    private long fee;
    private List<PayerVo> payers;
    private List<PayeeVo> nonChangePayees;
    private PayeeVo changePayee;
    private List<PayeeVo> payees;
    private TransactionDto transaction;




    //region get set
    public boolean isBuildTransactionSuccess() {
        return buildTransactionSuccess;
    }

    public void setBuildTransactionSuccess(boolean buildTransactionSuccess) {
        this.buildTransactionSuccess = buildTransactionSuccess;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }

    public List<PayeeVo> getNonChangePayees() {
        return nonChangePayees;
    }

    public void setNonChangePayees(List<PayeeVo> nonChangePayees) {
        this.nonChangePayees = nonChangePayees;
    }

    public PayeeVo getChangePayee() {
        return changePayee;
    }

    public void setChangePayee(PayeeVo changePayee) {
        this.changePayee = changePayee;
    }

    public List<PayerVo> getPayers() {
        return payers;
    }

    public void setPayers(List<PayerVo> payers) {
        this.payers = payers;
    }

    public List<PayeeVo> getPayees() {
        return payees;
    }

    public void setPayees(List<PayeeVo> payees) {
        this.payees = payees;
    }
    //endregion
}
