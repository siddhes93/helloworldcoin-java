package com.xingkaichun.helloworldblockchain.application.vo.wallet;

import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

import java.util.List;

/**
 * 构建交易响应
 *
 * @author 邢开春 409060350@qq.com
 */
public class AutomaticBuildTransactionResponse {
    //是否构建交易成功
    private boolean buildTransactionSuccess;
    //若失败，填写构建失败的原因
    private String message;

    //构建的交易哈希
    private String transactionHash;
    //交易手续费
    private long fee;
    //付款方
    private List<PayerVo> payers;
    //[非找零]收款方
    private List<PayeeVo> nonChangePayees;
    //[找零]收款方
    private PayeeVo changePayee;
    //收款方=[非找零]收款方+[找零]收款方
    private List<PayeeVo> payees;
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
}
