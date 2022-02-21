package com.helloworldcoin.application.vo.transaction;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryTransactionsByBlockHashTransactionHeightResponse {

    private List<TransactionVo> transactions;




    //region get set
    public List<TransactionVo> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionVo> transactions) {
        this.transactions = transactions;
    }
    //endregion
}
