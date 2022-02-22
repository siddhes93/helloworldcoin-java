package com.helloworldcoin.application.vo;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainBrowserApplicationApi {

    public static final String QUERY_BLOCKCHAIN_HEIGHT = "/Api/BlockchainBrowserApplication/QueryBlockchainHeight";



    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockchainBrowserApplication/QueryTransactionByTransactionHash";
    public static final String QUERY_TRANSACTIONS_BY_BLOCK_HASH_TRANSACTION_HEIGHT = "/Api/BlockchainBrowserApplication/QueryTransactionsByBlockHashTransactionHeight";


    public static final String QUERY_TRANSACTION_OUTPUT_BY_ADDRESS = "/Api/BlockchainBrowserApplication/QueryTransactionOutputByAddress";

    public static final String QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID = "/Api/BlockchainBrowserApplication/QueryTransactionOutputByTransactionOutputId";


    public static final String QUERY_UNCONFIRMED_TRANSACTIONS = "/Api/BlockchainBrowserApplication/QueryUnconfirmedTransactions";
    public static final String QUERY_UNCONFIRMED_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockchainBrowserApplication/QueryUnconfirmedTransactionByTransactionHash";


    public static final String QUERY_BLOCK_BY_BLOCK_HEIGHT = "/Api/BlockchainBrowserApplication/QueryBlockByBlockHeight";
    public static final String QUERY_BLOCK_BY_BLOCK_HASH = "/Api/BlockchainBrowserApplication/QueryBlockByBlockHash";
    public static final String QUERY_LATEST_10_BLOCKS = "/Api/BlockchainBrowserApplication/QueryLatest10Blocks";
}
