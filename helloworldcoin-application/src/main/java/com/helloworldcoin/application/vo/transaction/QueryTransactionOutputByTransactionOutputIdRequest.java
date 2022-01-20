package com.helloworldcoin.application.vo.transaction;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryTransactionOutputByTransactionOutputIdRequest {

    private String transactionHash;
    private long transactionOutputIndex;

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }
}
