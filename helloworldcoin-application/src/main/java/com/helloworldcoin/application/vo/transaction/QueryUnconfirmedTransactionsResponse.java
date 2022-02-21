package com.helloworldcoin.application.vo.transaction;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryUnconfirmedTransactionsResponse {

    private List<UnconfirmedTransactionVo> unconfirmedTransactions;




    //region get set
    public List<UnconfirmedTransactionVo> getUnconfirmedTransactions() {
        return unconfirmedTransactions;
    }

    public void setUnconfirmedTransactions(List<UnconfirmedTransactionVo> unconfirmedTransactions) {
        this.unconfirmedTransactions = unconfirmedTransactions;
    }
    //endregion
}
