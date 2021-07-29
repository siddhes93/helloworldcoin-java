package com.xingkaichun.helloworldblockchain.core.model.wallet;

import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

import java.util.List;

/**
 * 构建交易响应
 *
 * @author 邢开春 409060350@qq.com
 */
public class AutoBuildTransactionResponse {
    //是否构建交易成功
    private boolean buildTransactionSuccess;
    //若失败，填写构建失败的原因
    private String message;

    //构建的交易哈希
    private String transactionHash;
    //交易手续费
    private long fee;
    //付款方
    private List<Payer> payers;
    //[非找零]收款方
    private List<Payee> nonChangePayees;
    //[找零]收款方
    private Payee changePayee;
    //收款方=[非找零]收款方+[找零]收款方
    private List<Payee> payees;
    //构建的完整交易
    private TransactionDto transaction;


    public boolean isBuildTransactionSuccess() {
        return buildTransactionSuccess;
    }

    public void setBuildTransactionSuccess(boolean buildTransactionSuccess) {
        this.buildTransactionSuccess = buildTransactionSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
