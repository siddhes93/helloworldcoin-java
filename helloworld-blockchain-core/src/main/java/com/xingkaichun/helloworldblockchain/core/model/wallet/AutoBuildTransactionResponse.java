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
    //排除找零的收款方
    private List<Payee> exclusionChangePayees;
    //找零收款方
    private Payee changePayee;
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

    public List<Payee> getExclusionChangePayees() {
        return exclusionChangePayees;
    }

    public void setExclusionChangePayees(List<Payee> exclusionChangePayees) {
        this.exclusionChangePayees = exclusionChangePayees;
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
}
