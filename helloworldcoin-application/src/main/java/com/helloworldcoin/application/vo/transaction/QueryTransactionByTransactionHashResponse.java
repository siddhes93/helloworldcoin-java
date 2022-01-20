package com.helloworldcoin.application.vo.transaction;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryTransactionByTransactionHashResponse {

    private TransactionVo transaction;




    //region get set
    public TransactionVo getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionVo transaction) {
        this.transaction = transaction;
    }
    //endregion

}
