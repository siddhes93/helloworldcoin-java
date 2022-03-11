package com.helloworldcoin.core;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.TransactionDto;


/**
 * Blockchain Database
 * This class is used for persistence of blockchain data.
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class BlockchainDatabase {

    //region
    protected CoreConfiguration coreConfiguration;
    protected Consensus consensus ;
    protected Incentive incentive ;
    protected VirtualMachine virtualMachine;
    //endregion



    //region add block、delete block
    /**
     * Add a block to the tail of the blockchain.
     */
    public abstract boolean addBlockDto(BlockDto blockDto) ;
    /**
     * Delete the tail block (the last block) of the blockchain
     */
    public abstract void deleteTailBlock() ;
    /**
     * Delete blocks with block height greater than or equal to @blockHeight@
     */
    public abstract void deleteBlocks(long blockHeight) ;
    //endregion



    //region check blocks, check transactions
    /**
     * Check if a block can be added to the blockchain
     * There is only one case where a block can be added to the blockchain, namely: the block is the next block on the blockchain.
     */
    public abstract boolean checkBlock(Block block) ;
    /**
     * Check that the transaction can be added to the next block.
     */
    public abstract boolean checkTransaction(Transaction transaction) ;
    //endregion



    //region Blockchain query related
    /**
     * query Blockchain Height
     */
    public abstract long queryBlockchainHeight() ;
    /**
     * Query the total number of transactions in the blockchain
     */
    public abstract long queryBlockchainTransactionHeight() ;
    /**
     * Query the total number of transaction outputs in the blockchain
     */
    public abstract long queryBlockchainTransactionOutputHeight() ;
    //endregion



    //region block query related
    /**
     * Query the last block on the blockchain
     */
    public abstract Block queryTailBlock() ;
    /**
     * query Block By Block Height
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight) ;
    /**
     * query Block By Block Hash
     */
    public abstract Block queryBlockByBlockHash(String blockHash) ;
    //endregion



    //region transaction query related
    /**
     * query Transaction By Transaction Height. The transaction height starts at 1.
     */
    public abstract Transaction queryTransactionByTransactionHeight(long transactionHeight) ;
    /**
     * query Transaction By TransactionHash
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;
    /**
     * query Source Transaction By Transaction Output Id
     * Source Transaction: The transaction from which the transaction output was generated
     */
    public abstract Transaction querySourceTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    /**
     * query Destination Transaction By Transaction Output Id
     * Destination Transaction: The transaction that use the transaction output
     */
    public abstract Transaction queryDestinationTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    //endregion



    //region Transaction output query related
    /**
     * query Transaction Output By Transaction Output Height
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputHeight(long transactionOutputHeight) ;
    /**
     * query Transaction Output By Transaction Output Id
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    /**
     * query Unspent Transaction Output By Transaction Output Id
     */
    public abstract TransactionOutput queryUnspentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    /**
     * query Spent Transaction Output By Transaction Output Id
     */
    public abstract TransactionOutput querySpentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) ;
    //endregion



    //region Address query related
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
    //endregion

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
    public Incentive getIncentive() {
        return incentive;
    }

    public Consensus getConsensus() {
        return consensus;
    }
    //endregion
}