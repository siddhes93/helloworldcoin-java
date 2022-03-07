package com.helloworldcoin.core.model.transaction;

/**
 * Transaction Type
 * There are only two types of transactions: coinbase transaction, standard transaction.
 * Please do not increase or decrease transaction types.
 *
 * For Coinbase transactions, the payer must be 0 and the payee must be 1.
 * For standard transactions, the payer is at least 1 person, and multiple people are allowed,
 * and the payee is at least 1 person, and multiple people are allowed.
 *
 * @author x.king xdotking@gmail.com
 */
public enum TransactionType {

    /**
     * Coinbase transaction: The first transaction of each block has a name called the coinbase transaction.
     * Each block has one and only one coinbase transaction.
     * Coinbase transactions are used to issue miners mining incentives.
     */
    COINBASE_TRANSACTION,
    /**
     * standard transaction
     */
    STANDARD_TRANSACTION
}
