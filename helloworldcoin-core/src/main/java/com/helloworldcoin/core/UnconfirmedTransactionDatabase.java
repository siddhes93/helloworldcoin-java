package com.helloworldcoin.core;

import com.helloworldcoin.netcore.dto.TransactionDto;

import java.util.List;

/**
 * Unconfirmed Transaction Database
 * All transactions that are not persisted to the blockchain should be collected as much as possible.
 * Other objects can get unconfirmed transaction data from here, and then do their own work. For example,
 * miners can obtain mining raw materials (unconfirmed transaction data) from here for mining work.
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class UnconfirmedTransactionDatabase {

    protected CoreConfiguration coreConfiguration;


    /**
     * insert Transaction
     */
    public abstract boolean insertTransaction(TransactionDto transaction) ;

    /**
     * select Transactions
     */
    public abstract List<TransactionDto> selectTransactions(long from, long size) ;

    /**
     * delete Transaction
     */
    public abstract void deleteByTransactionHash(String transactionHash) ;

    /**
     * select Transaction By Transaction Hash
     */
    public abstract TransactionDto selectTransactionByTransactionHash(String transactionHash);
}
