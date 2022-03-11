package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.*;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionRequest;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionResponse;
import com.helloworldcoin.core.tool.Model2DtoTool;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.util.LogUtil;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainCoreImpl extends BlockchainCore {

    public BlockchainCoreImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase, Wallet wallet, Miner miner) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
        this.unconfirmedTransactionDatabase = unconfirmedTransactionDatabase;
        this.wallet = wallet;
        this.miner = miner;
    }

    @Override
    public void start() {
        new Thread(
                ()->{
                    try {
                        miner.start();
                    } catch (Exception e) {
                        LogUtil.error("start miner error.",e);
                    }
                }
        ).start();
    }

    @Override
    public long queryBlockchainHeight() {
        return blockchainDatabase.queryBlockchainHeight();
    }


    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        return blockchainDatabase.queryTransactionByTransactionHash(transactionHash);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        return blockchainDatabase.queryTransactionByTransactionHeight(transactionHeight);
    }

    @Override
    public TransactionOutput queryTransactionOutputByAddress(String address) {
        return blockchainDatabase.queryTransactionOutputByAddress(address);
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByAddress(String address) {
        return blockchainDatabase.queryUnspentTransactionOutputByAddress(address);
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByAddress(String address) {
        return blockchainDatabase.querySpentTransactionOutputByAddress(address);
    }

    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        return blockchainDatabase.queryBlockByBlockHeight(blockHeight);
    }

    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        return blockchainDatabase.queryBlockByBlockHash(blockHash);
    }

    @Override
    public Block queryTailBlock() {
        return blockchainDatabase.queryTailBlock();
    }

    @Override
    public void deleteTailBlock() {
        blockchainDatabase.deleteTailBlock();
    }

    @Override
    public boolean addBlockDto(BlockDto blockDto) {
        return blockchainDatabase.addBlockDto(blockDto);
    }

    @Override
    public boolean addBlock(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return addBlockDto(blockDto);
    }


    @Override
    public void deleteBlocks(long blockHeight) {
        blockchainDatabase.deleteBlocks(blockHeight);
    }


    @Override
    public AutoBuildTransactionResponse autoBuildTransaction(AutoBuildTransactionRequest request) {
        return wallet.autoBuildTransaction(request);
    }

    @Override
    public void postTransaction(TransactionDto transactionDto) {
        unconfirmedTransactionDatabase.insertTransaction(transactionDto);
    }

    @Override
    public List<TransactionDto> queryUnconfirmedTransactions(long from, long size) {
        return unconfirmedTransactionDatabase.selectTransactions(from,size);
    }

    @Override
    public TransactionDto queryUnconfirmedTransactionByTransactionHash(String transactionHash) {
        return unconfirmedTransactionDatabase.selectTransactionByTransactionHash(transactionHash);
    }

    //region
    /**
     * block dto to block model
     */
    public Block blockDto2Block(BlockDto blockDto) {
        return blockchainDatabase.blockDto2Block(blockDto);
    }
    /**
     * transaction dto to transaction model
     */
    public Transaction transactionDto2Transaction(TransactionDto transactionDto) {
        return blockchainDatabase.transactionDto2Transaction(transactionDto);
    }
    //endregion
}