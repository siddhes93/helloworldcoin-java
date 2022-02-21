package com.helloworldcoin.application.vo.transaction;

import com.helloworldcoin.application.vo.framwork.PageCondition;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryUnconfirmedTransactionsRequest {

    private PageCondition pageCondition;




    //region get set
    public PageCondition getPageCondition() {
        return pageCondition;
    }

    public void setPageCondition(PageCondition pageCondition) {
        this.pageCondition = pageCondition;
    }
    //endregion
}
