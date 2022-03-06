package com.helloworldcoin.core.model.wallet;

import com.helloworldcoin.netcore.dto.TransactionDto;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class AutoBuildTransactionResponse {

    private boolean buildTransactionSuccess;

    //Constructed transaction hash
    private String transactionHash;
    //Transaction Fees
    private long fee;
    //payer
    private List<Payer> payers;
    //Non-change Payee
    private List<Payee> nonChangePayees;
    //Change Payee
    private Payee changePayee;
    //Payee = [non-change] payee + [change] payee
    private List<Payee> payees;
    //Constructed complete transaction
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

    public List<Payee> getNonChangePayees() {
        return nonChangePayees;
    }

    public void setNonChangePayees(List<Payee> nonChangePayees) {
        this.nonChangePayees = nonChangePayees;
    }

    public Payee getChangePayee() {
        return changePayee;
    }

    public void setChangePayee(Payee changePayee) {
        this.changePayee = changePayee;
    }

    public List<Payer> getPayers() {
        return payers;
    }

    public void setPayers(List<Payer> payers) {
        this.payers = payers;
    }

    public List<Payee> getPayees() {
        return payees;
    }

    public void setPayees(List<Payee> payees) {
        this.payees = payees;
    }
    //endregion
}
