package com.helloworldcoin.application.vo.transaction;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryUnconfirmedTransactionByTransactionHashResponse {

    private UnconfirmedTransactionVo transaction;




    //region get set
    public UnconfirmedTransactionVo getTransaction() {
        return transaction;
    }

    public void setTransaction(UnconfirmedTransactionVo transaction) {
        this.transaction = transaction;
    }
    //endregion

}
