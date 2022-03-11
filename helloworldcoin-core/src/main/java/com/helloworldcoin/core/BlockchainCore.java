package com.helloworldcoin.core;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionRequest;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionResponse;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.TransactionDto;

import java.util.List;

/**
 * Blockchain core
 * This class represents a complete stand-alone version (without network interaction function) of the blockchain core system,
 * which maintains the complete data of a blockchain at the bottom.
 *
 * At the beginning of the design, in order to simplify, it is designed without network module.
 * Except that it does not contain network modules, it contains all the functions that a blockchain system should have, including
 * 1. Blockchain account generation 2. Transfer 3. Submit transaction to blockchain
 * 4. Mining 5. Add blocks to the blockchain 6. Data verification: block verification, transaction verification
 * 7. On-chain block rollback 8. On-chain block query, transaction query, account fund query... etc.
 *
 * Blockchain core consists of the following parts:
 * @see BlockchainDatabase
 * @see UnconfirmedTransactionDatabase
 * @see Miner
 * @see Wallet
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class BlockchainCore {

    //region
    protected CoreConfiguration coreConfiguration;
    protected BlockchainDatabase blockchainDatabase ;
    protected UnconfirmedTransactionDatabase unconfirmedTransactionDatabase;
    protected Miner miner ;
    protected Wallet wallet ;
    //endregion




    /**
     * start BlockchainCore
     */
    public abstract void start();



    /**
     * Add a block to the tail of the blockchain.
     */
    public abstract boolean addBlockDto(BlockDto blockDto);
    /**
     * Add a block to the tail of the blockchain.
     */
    public abstract boolean addBlock(Block block);
    /**
     * Delete the tail block (the last block) of the blockchain
     */
    public abstract void deleteTailBlock();
    /**
     * Delete blocks with block height greater than or equal to @blockHeight@
     */
    public abstract void deleteBlocks(long blockHeight) ;
    /**
     * query Blockchain Height
     */
    public abstract long queryBlockchainHeight() ;
    /**
     * query Block By Block Height
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight);
    /**
     * query Block By Block Hash
     */
    public abstract Block queryBlockByBlockHash(String blockHash);
    /**
     * query Tail Block
     */
    public abstract Block queryTailBlock();




    /**
     * query Transaction By Transaction Hash
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;
    /**
     * query Transaction By Transaction Height
     * @param transactionHeight  transaction height starts at 1.
     */
    public abstract Transaction queryTransactionByTransactionHeight(long transactionHeight) ;




    /**
     * query Transaction Output By Address
     */
    public abstract TransactionOutput queryTransactionOutputByAddress(String address) ;
    /**
     * query Unspent Transaction Output By Address
     */
    public abstract TransactionOutput queryUnspentTransactionOutputByAddress(String address) ;
    /**
     * query Spent Transaction Output By Address
     */
    public abstract TransactionOutput querySpentTransactionOutputByAddress(String address) ;




    /**
     * Build Transaction
     */
    public abstract AutoBuildTransactionResponse autoBuildTransaction(AutoBuildTransactionRequest request) ;
    /**
     * Post Transaction To Blockchain
     */
    public abstract void postTransaction(TransactionDto transactionDto) ;
    /**
     * query Unconfirmed Transactions
     */
    public abstract List<TransactionDto> queryUnconfirmedTransactions(long from, long size) ;
    /**
     * query Unconfirmed Transaction By Transaction Hash
     */
    public abstract TransactionDto queryUnconfirmedTransactionByTransactionHash(String transactionHash) ;



    //region
    /**
     * block dto to block model
     */
    public abstract Block blockDto2Block(BlockDto blockDto) ;
    /**
     * transaction dto to transaction model
     */
    public abstract Transaction transactionDto2Transaction(TransactionDto transactionDto) ;
    //endregion


    //region get set
    public BlockchainDatabase getBlockchainDatabase() {
        return blockchainDatabase;
    }

    public UnconfirmedTransactionDatabase getUnconfirmedTransactionDatabase() {
        return unconfirmedTransactionDatabase;
    }

    public Miner getMiner() {
        return miner;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public CoreConfiguration getCoreConfiguration() {
        return coreConfiguration;
    }
    //endregion
}