package com.helloworldcoin.netcore.dto;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class PostTransactionRequest {

    private TransactionDto transaction;

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }
}
