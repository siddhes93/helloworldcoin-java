package com.helloworldcoin.application.service;

import com.helloworldcoin.application.vo.block.BlockVo;
import com.helloworldcoin.application.vo.transaction.TransactionOutputVo3;
import com.helloworldcoin.application.vo.transaction.TransactionVo;
import com.helloworldcoin.application.vo.transaction.UnconfirmedTransactionVo;

import java.util.List;

/**
 * 区块链浏览器应用service
 *
 * @author x.king xdotking@gmail.com
 */
public interface BlockchainBrowserApplicationService {

    /**
     * 根据交易输出ID获取交易输出
     */
    TransactionOutputVo3 queryTransactionOutputByTransactionOutputId(String transactionHash, long transactionOutputIndex);
    /**
     * 根据地址获取交易输出
     */
    TransactionOutputVo3 queryTransactionOutputByAddress(String address);

    /**
     * 根据交易哈希查询交易
     */
    TransactionVo queryTransactionByTransactionHash(String transactionHash);
    /**
     * 根据区块哈希与交易高度查询交易列表
     */
    List<TransactionVo> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size);

     /**
     * 根据区块哈希查找区块
     */
    BlockVo queryBlockViewByBlockHeight(long blockHeight);

    /**
     * 根据交易哈希查找未确认的交易
     */
    UnconfirmedTransactionVo queryUnconfirmedTransactionByTransactionHash(String transactionHash);
}
