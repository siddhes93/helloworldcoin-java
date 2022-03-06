package com.helloworldcoin.core.model.transaction;

/**
 * Transaction Type
 * There are only two types of transactions: genesis transaction, standard transaction.
 * Please do not increase or decrease transaction types.
 *
 * For Genesis transactions, the payer must be 0 and the payee must be 1.
 * For standard transactions, the payer is at least 1 person, and multiple people are allowed,
 * and the payee is at least 1 person, and multiple people are allowed.
 *
 * @author x.king xdotking@gmail.com
 */
public enum TransactionType {

    /**
     * TODO COINBASE_TRANSACTION
     * Genesis transaction: The first transaction of each block has a name called the genesis transaction.
     * Each block has one and only one genesis transaction.
     * Genesis transactions are used to issue miners mining incentives.
     */
    GENESIS_TRANSACTION,
    /**
     * standard transaction
     */
    STANDARD_TRANSACTION
}
