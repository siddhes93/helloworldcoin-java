package com.helloworldcoin.application.service;

import com.helloworldcoin.application.vo.block.BlockVo;
import com.helloworldcoin.application.vo.transaction.TransactionOutputVo3;
import com.helloworldcoin.application.vo.transaction.TransactionVo;
import com.helloworldcoin.application.vo.transaction.UnconfirmedTransactionVo;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public interface BlockchainBrowserApplicationService {

    TransactionOutputVo3 queryTransactionOutputByTransactionOutputId(String transactionHash, long transactionOutputIndex);
    TransactionOutputVo3 queryTransactionOutputByAddress(String address);


    TransactionVo queryTransactionByTransactionHash(String transactionHash);
    List<TransactionVo> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size);

    BlockVo queryBlockViewByBlockHeight(long blockHeight);

    UnconfirmedTransactionVo queryUnconfirmedTransactionByTransactionHash(String transactionHash);
}
