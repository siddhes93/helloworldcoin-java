package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class UnconfirmedTransactionVo {

    private String transactionHash;
    private List<TransactionInputVo2> transactionInputs;
    private List<TransactionOutputVo2> transactionOutputs;

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public List<TransactionInputVo2> getTransactionInputs() {
        return transactionInputs;
    }

    public void setTransactionInputs(List<TransactionInputVo2> transactionInputs) {
        this.transactionInputs = transactionInputs;
    }

    public List<TransactionOutputVo2> getTransactionOutputs() {
        return transactionOutputs;
    }

    public void setTransactionOutputs(List<TransactionOutputVo2> transactionOutputs) {
        this.transactionOutputs = transactionOutputs;
    }
}
