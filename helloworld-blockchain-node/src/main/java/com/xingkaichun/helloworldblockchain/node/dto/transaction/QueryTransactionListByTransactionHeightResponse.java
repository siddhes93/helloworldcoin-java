package com.xingkaichun.helloworldblockchain.node.dto.transaction;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryTransactionListByTransactionHeightResponse {

    private List<Transaction> transactionList;




    //region get set

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    //endregion
}
