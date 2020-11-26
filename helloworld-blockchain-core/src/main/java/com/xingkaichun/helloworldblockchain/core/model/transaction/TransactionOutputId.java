package com.xingkaichun.helloworldblockchain.core.model.transaction;

import java.io.Serializable;

public class TransactionOutputId implements Serializable {
    /**
     * 交易哈希
     * 冗余
     */
    private String transactionHash;
    /**
     * 交易输出序列号
     * 冗余
     * 在这个交易中的的排序号
     */
    private long transactionOutputIndex;


    public String getTransactionOutputId() {
        return transactionHash + "|" + transactionOutputIndex;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
