package com.helloworldcoin.application.vo;

/**
 * 区块链浏览器应用接口
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainBrowserApplicationApi {

    //查询区块链高度
    public static final String QUERY_BLOCKCHAIN_HEIGHT = "/Api/BlockchainBrowserApplication/QueryBlockchainHeight";



    //根据交易哈希查询交易
    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockchainBrowserApplication/QueryTransactionByTransactionHash";
    //根据区块哈希与交易高度查询交易列表
    public static final String QUERY_TRANSACTIONS_BY_BLOCK_HASH_TRANSACTION_HEIGHT = "/Api/BlockchainBrowserApplication/QueryTransactionsByBlockHashTransactionHeight";


    //根据地址获取交易输出
    public static final String QUERY_TRANSACTION_OUTPUT_BY_ADDRESS = "/Api/BlockchainBrowserApplication/QueryTransactionOutputByAddress";

    //根据交易输出ID获取交易输出
    public static final String QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID = "/Api/BlockchainBrowserApplication/QueryTransactionOutputByTransactionOutputId";


    //查询未确认交易
    public static final String QUERY_UNCONFIRMED_TRANSACTIONS = "/Api/BlockchainBrowserApplication/QueryUnconfirmedTransactions";
    //根据交易哈希查询未确认交易
    public static final String QUERY_UNCONFIRMED_TRANSACTION_BY_TRANSACTION_HASH = "/Api/BlockchainBrowserApplication/QueryUnconfirmedTransactionByTransactionHash";


    //根据区块高度查询区块
    public static final String QUERY_BLOCK_BY_BLOCK_HEIGHT = "/Api/BlockchainBrowserApplication/QueryBlockByBlockHeight";
    //根据区块哈希查询区块
    public static final String QUERY_BLOCK_BY_BLOCK_HASH = "/Api/BlockchainBrowserApplication/QueryBlockByBlockHash";
    //查询最新的10个区块
    public static final String QUERY_TOP10_BLOCKS = "/Api/BlockchainBrowserApplication/QueryTop10Blocks";
}
