package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
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
