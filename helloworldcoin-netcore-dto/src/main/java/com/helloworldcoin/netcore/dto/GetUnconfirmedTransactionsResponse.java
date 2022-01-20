package com.helloworldcoin.netcore.dto;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class GetUnconfirmedTransactionsResponse {

    private List<TransactionDto> transactions;

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }
}
