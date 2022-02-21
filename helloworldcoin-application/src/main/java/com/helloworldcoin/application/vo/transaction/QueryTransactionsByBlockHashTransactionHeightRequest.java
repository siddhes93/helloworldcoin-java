package com.helloworldcoin.application.vo.transaction;

import com.helloworldcoin.application.vo.framwork.PageCondition;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryTransactionsByBlockHashTransactionHeightRequest {

    private String blockHash;
    private PageCondition pageCondition;




    //region get set
    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public PageCondition getPageCondition() {
        return pageCondition;
    }

    public void setPageCondition(PageCondition pageCondition) {
        this.pageCondition = pageCondition;
    }
    //endregion
}
