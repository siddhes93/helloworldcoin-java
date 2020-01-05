package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 * 本来的作用是收集交易，用于挖矿。
 * 所有没有持久化进区块链的交易，都应该被收集起来，供挖矿使用。
 * 其它对象可以从本类获取交易数据，然后进行自己的活动。例如矿工可以从交易池获取挖矿的原材料(交易数据)进行挖矿活动。
 */
public interface ForMinerTransactionDataBase {

    /**
     * 新增交易
     */
    boolean insertTransaction(Transaction transaction) throws Exception ;

    /**
     * 新增交易
     */
    boolean insertTransactionList(List<Transaction> transactionList) throws Exception ;

    /**
     * 获取交易
     */
    List<Transaction> selectTransactionList(int from, int size) throws Exception ;

    /**
     * 删除交易
     */
    void deleteTransaction(Transaction transaction) throws Exception ;

    /**
     * 删除交易
     */
    void deleteTransactionList(List<Transaction> transactionList) throws Exception ;
}
