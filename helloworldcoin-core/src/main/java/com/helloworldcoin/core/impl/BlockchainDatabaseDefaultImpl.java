package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.*;
import com.helloworldcoin.core.model.script.*;
import com.helloworldcoin.core.tool.*;
import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.util.*;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.enums.BlockchainAction;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionInput;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.transaction.TransactionType;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.setting.GenesisBlockSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainDatabaseDefaultImpl extends BlockchainDatabase {

    //region
    private static final String BLOCKCHAIN_DATABASE_NAME = "BlockchainDatabase";

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BlockchainDatabaseDefaultImpl(CoreConfiguration coreConfiguration, Incentive incentive, Consensus consensus, VirtualMachine virtualMachine) {
        this.coreConfiguration = coreConfiguration;
        this.consensus = consensus;
        this.incentive = incentive;
        this.virtualMachine = virtualMachine;
    }
    //endregion



    //region add block、delete block
    @Override
    public boolean addBlockDto(BlockDto blockDto) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            Block block = blockDto2Block(blockDto);
            boolean checkBlock = checkBlock(block);
            if(!checkBlock){
                return false;
            }
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(block, BlockchainAction.ADD_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(), kvWriteBatch);
            return true;
        }catch (Exception e){
            LogUtil.debug("add block error.");
            return false;
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void deleteTailBlock() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            Block tailBlock = queryTailBlock();
            if(tailBlock == null){
                return;
            }
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainAction.DELETE_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(),kvWriteBatch);
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void deleteBlocks(long blockHeight) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            while (true){
                Block tailBlock = queryTailBlock();
                if(tailBlock == null){
                    return;
                }
                if(tailBlock.getHeight() < blockHeight){
                    return;
                }
                KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainAction.DELETE_BLOCK);
                KvDbUtil.write(getBlockchainDatabasePath(),kvWriteBatch);
            }
        }finally {
            writeLock.unlock();
        }
    }
    //endregion



    //region
    @Override
    public boolean checkBlock(Block block) {

        //check block structure
        if(!StructureTool.checkBlockStructure(block)){
            LogUtil.debug("The block data is abnormal. Please verify the block structure.");
            return false;
        }
        //check block size
        if(!SizeTool.checkBlockSize(block)){
            LogUtil.debug("The block data is abnormal, please check the size of the block.");
            return false;
        }


        //check business
        Block previousBlock = queryTailBlock();
        //check block height
        if(!BlockTool.checkBlockHeight(previousBlock,block)){
            LogUtil.debug("Wrong block height for block write.");
            return false;
        }
        //check previous block hash
        if(!BlockTool.checkPreviousBlockHash(previousBlock,block)){
            LogUtil.debug("The previous block hash of the block write was wrong.");
            return false;
        }
        //check block timestamp
        if(!BlockTool.checkBlockTimestamp(previousBlock,block)){
            LogUtil.debug("Block generation is too late.");
            return false;
        }
        //check block new hash
        if(!checkBlockNewHash(block)){
            LogUtil.debug("The block data is abnormal, and the newly generated hash in the block is abnormal.");
            return false;
        }
        //check block new address
        if(!checkBlockNewAddress(block)){
            LogUtil.debug("The block data is abnormal, and the newly generated hash in the block is abnormal.");
            return false;
        }
        //check block double spend
        if(!checkBlockDoubleSpend(block)){
            LogUtil.debug("The block data is abnormal, and a double-spending attack is detected.");
            return false;
        }
        //check consensus
        if(!consensus.checkConsensus(this,block)){
            LogUtil.debug("The block data is abnormal and the consensus rules are not met.");
            return false;
        }
        //check incentive
        if(!incentive.checkIncentive(this,block)){
            LogUtil.debug("The block data is abnormal, and the incentive verification fails.");
            return false;
        }

        //check transaction
        for(Transaction transaction : block.getTransactions()){
            boolean transactionCanAddToNextBlock = checkTransaction(transaction);
            if(!transactionCanAddToNextBlock){
                LogUtil.debug("The block data is abnormal, and the transaction is abnormal.");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkTransaction(Transaction transaction) {
        //check Transaction Structure
        if(!StructureTool.checkTransactionStructure(transaction)){
            LogUtil.debug("The transaction data is abnormal, please verify the structure of the transaction.");
            return false;
        }
        //check Transaction Size
        if(!SizeTool.checkTransactionSize(transaction)){
            LogUtil.debug("The transaction data is abnormal, please check the size of the transaction.");
            return false;
        }


        //Check if the address in the transaction is a P2PKH address
        if(!TransactionTool.checkPayToPublicKeyHashAddress(transaction)){
            return false;
        }
        //Check if the script in the transaction is a P2PKH script
        if(!TransactionTool.checkPayToPublicKeyHashScript(transaction)){
            return false;
        }

        //business verification
        //check Transaction New Hash
        if(!checkTransactionNewHash(transaction)){
            LogUtil.debug("The block data is abnormal, and the newly generated hash in the block is abnormal.");
            return false;
        }
        //check Transaction New Address
        if(!checkTransactionNewAddress(transaction)){
            LogUtil.debug("The block data is abnormal, and the newly generated hash in the block is abnormal.");
            return false;
        }
        //check Transaction Value
        if(!TransactionTool.checkTransactionValue(transaction)){
            LogUtil.debug("The block data is abnormal and the transaction amount is illegal");
            return false;
        }
        //check Transaction Double Spend
        if(!checkTransactionDoubleSpend(transaction)){
            LogUtil.debug("The transaction data is abnormal, and a double-spending attack is detected.");
            return false;
        }
        //check Transaction Script
        if(!checkTransactionScript(transaction)) {
            LogUtil.debug("Transaction verification failed: transaction [input script] unlock transaction [output script] exception.");
            return false;
        }
        return true;
    }
    //endregion



    //region Blockchain query related
    @Override
    public long queryBlockchainHeight() {
        byte[] bytesBlockchainHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainHeightKey());
        if(bytesBlockchainHeight == null){
            return GenesisBlockSetting.HEIGHT;
        }
        return ByteUtil.bytesToUint64(bytesBlockchainHeight);
    }

    @Override
    public long queryBlockchainTransactionHeight() {
        byte[] byteTotalTransactionCount = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.bytesToUint64(byteTotalTransactionCount);
    }
    @Override
    public long queryBlockchainTransactionOutputHeight() {
        byte[] byteTotalTransactionCount = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.bytesToUint64(byteTotalTransactionCount);
    }
    //endregion



    //region
    @Override
    public Block queryTailBlock() {
        long blockchainHeight = queryBlockchainHeight();
        if(blockchainHeight <= GenesisBlockSetting.HEIGHT){
            return null;
        }
        return queryBlockByBlockHeight(blockchainHeight);
    }
    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        byte[] bytesBlock = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeTool.decode(bytesBlock,Block.class);
    }
    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        byte[] bytesBlockHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(blockHash));
        if(bytesBlockHeight == null){
            return null;
        }
        return queryBlockByBlockHeight(ByteUtil.bytesToUint64(bytesBlockHeight));
    }
    //endregion



    //region Transaction query related
    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        byte[] transactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transactionHash));
        if(transactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.bytesToUint64(transactionHeight));
    }

    @Override
    public Transaction querySourceTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] sourceTransactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToSourceTransactionHeightKey(transactionHash,transactionOutputIndex));
        if(sourceTransactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.bytesToUint64(sourceTransactionHeight));
    }

    @Override
    public Transaction queryDestinationTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] destinationTransactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToDestinationTransactionHeightKey(transactionHash,transactionOutputIndex));
        if(destinationTransactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.bytesToUint64(destinationTransactionHeight));
    }

    @Override
    public TransactionOutput queryTransactionOutputByTransactionOutputHeight(long transactionOutputHeight) {
        byte[] bytesTransactionOutput = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputHeightToTransactionOutputKey(transactionOutputHeight));
        if(bytesTransactionOutput == null){
            return null;
        }
        return EncodeDecodeTool.decode(bytesTransactionOutput,TransactionOutput.class);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        byte[] byteTransaction = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transactionHeight));
        if(byteTransaction == null){
            return null;
        }
        return EncodeDecodeTool.decode(byteTransaction,Transaction.class);
    }
    //endregion



    //region Transaction Output Query related
    @Override
    public TransactionOutput queryTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToTransactionOutputHeightKey(transactionHash,transactionOutputIndex));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.bytesToUint64(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(transactionHash,transactionOutputIndex));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.bytesToUint64(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(transactionHash,transactionOutputIndex));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.bytesToUint64(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressToTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.bytesToUint64(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.bytesToUint64(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressToSpentTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.bytesToUint64(bytesTransactionOutputHeight));
    }
    //endregion



    //region Assemble WriteBatch
    private KvDbUtil.KvWriteBatch createBlockWriteBatch(Block block, BlockchainAction blockchainAction) {
        KvDbUtil.KvWriteBatch kvWriteBatch = new KvDbUtil.KvWriteBatch();

        storeHash(kvWriteBatch,block, blockchainAction);
        storeAddress(kvWriteBatch,block, blockchainAction);

        storeBlockchainHeight(kvWriteBatch,block, blockchainAction);
        storeBlockchainTransactionHeight(kvWriteBatch,block, blockchainAction);
        storeBlockchainTransactionOutputHeight(kvWriteBatch,block, blockchainAction);

        storeBlockHeightToBlock(kvWriteBatch,block, blockchainAction);
        storeBlockHashToBlockHeight(kvWriteBatch,block, blockchainAction);

        storeTransactionHeightToTransaction(kvWriteBatch,block, blockchainAction);
        storeTransactionHashToTransactionHeight(kvWriteBatch,block, blockchainAction);

        storeTransactionOutputHeightToTransactionOutput(kvWriteBatch,block, blockchainAction);
        storeTransactionOutputIdToTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeTransactionOutputIdToUnspentTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeTransactionOutputIdToSpentTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeTransactionOutputIdToSourceTransactionHeight(kvWriteBatch,block, blockchainAction);
        storeTransactionOutputIdToDestinationTransactionHeight(kvWriteBatch,block, blockchainAction);

        storeAddressToTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeAddressToUnspentTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeAddressToSpentTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        return kvWriteBatch;
    }


    private void storeTransactionOutputIdToSourceTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput transactionOutput:outputs){
                        byte[] transactionOutputIdToToSourceTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSourceTransactionHeightKey(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(transactionOutputIdToToSourceTransactionHeightKey, ByteUtil.uint64ToBytes(transaction.getTransactionHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToToSourceTransactionHeightKey);
                        }
                    }
                }
            }
        }
    }
    private void storeTransactionOutputIdToDestinationTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToToDestinationTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToDestinationTransactionHeightKey(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(transactionOutputIdToToDestinationTransactionHeightKey, ByteUtil.uint64ToBytes(transaction.getTransactionHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToToDestinationTransactionHeightKey);
                        }
                    }
                }
            }
        }
    }
    private void storeTransactionOutputIdToTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToTransactionOutputHeightKey(output.getTransactionHash(),output.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(transactionOutputIdToTransactionOutputHeightKey, ByteUtil.uint64ToBytes(output.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToTransactionOutputHeightKey);
                        }
                    }
                }
            }
        }
    }
    private void storeTransactionOutputHeightToTransactionOutput(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputHeightToTransactionOutputKey = BlockchainDatabaseKeyTool.buildTransactionOutputHeightToTransactionOutputKey(output.getTransactionOutputHeight());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(transactionOutputHeightToTransactionOutputKey, EncodeDecodeTool.encode(output));
                        } else {
                            kvWriteBatch.delete(transactionOutputHeightToTransactionOutputKey);
                        }
                    }
                }
            }
        }
    }
    private void storeTransactionOutputIdToUnspentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.delete(transactionOutputIdToUnspentTransactionOutputHeightKey);
                        } else {
                            kvWriteBatch.put(transactionOutputIdToUnspentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(unspentTransactionOutput.getTransactionOutputHeight()));
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(output.getTransactionHash(),output.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(transactionOutputIdToUnspentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(output.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToUnspentTransactionOutputHeightKey);
                        }
                    }
                }
            }
        }
    }
    private void storeTransactionOutputIdToSpentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(transactionOutputIdToSpentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(unspentTransactionOutput.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToSpentTransactionOutputHeightKey);
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(output.getTransactionHash(),output.getTransactionOutputIndex());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.delete(transactionOutputIdToSpentTransactionOutputHeightKey);
                        } else {
                            kvWriteBatch.put(transactionOutputIdToSpentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(output.getTransactionOutputHeight()));
                        }
                    }
                }
            }
        }
    }
    private void storeTransactionHeightToTransaction(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                byte[] transactionHeightToTransactionKey = BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transaction.getTransactionHeight());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHeightToTransactionKey, EncodeDecodeTool.encode(transaction));
                } else {
                    kvWriteBatch.delete(transactionHeightToTransactionKey);
                }
            }
        }
    }
    private void storeTransactionHashToTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                byte[] transactionHashToTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transaction.getTransactionHash());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHashToTransactionHeightKey, ByteUtil.uint64ToBytes(transaction.getTransactionHeight()));
                } else {
                    kvWriteBatch.delete(transactionHashToTransactionHeightKey);
                }
            }
        }
    }
    private void storeBlockchainHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockchainHeightKey = BlockchainDatabaseKeyTool.buildBlockchainHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.uint64ToBytes(block.getHeight()));
        }else{
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.uint64ToBytes(block.getHeight()-1));
        }
    }
    private void storeBlockHashToBlockHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHashBlockHeightKey = BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(block.getHash());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHashBlockHeightKey, ByteUtil.uint64ToBytes(block.getHeight()));
        }else{
            kvWriteBatch.delete(blockHashBlockHeightKey);
        }
    }
    private void storeBlockchainTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        long transactionCount = queryBlockchainTransactionHeight();
        byte[] bytesBlockchainTransactionCountKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.uint64ToBytes(transactionCount + BlockTool.getTransactionCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.uint64ToBytes(transactionCount - BlockTool.getTransactionCount(block)));
        }
    }
    private void storeBlockchainTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        long transactionOutputCount = queryBlockchainTransactionOutputHeight();
        byte[] bytesBlockchainTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.uint64ToBytes(transactionOutputCount + BlockTool.getTransactionOutputCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.uint64ToBytes(transactionOutputCount - BlockTool.getTransactionOutputCount(block)));
        }
    }
    private void storeBlockHeightToBlock(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHeightKey = BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(block.getHeight());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHeightKey, EncodeDecodeTool.encode(block));
        }else{
            kvWriteBatch.delete(blockHeightKey);
        }
    }

    private void storeHash(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHashKey = BlockchainDatabaseKeyTool.buildHashKey(block.getHash());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHashKey, blockHashKey);
        } else {
            kvWriteBatch.delete(blockHashKey);
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                byte[] transactionHashKey = BlockchainDatabaseKeyTool.buildHashKey(transaction.getTransactionHash());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHashKey, transactionHashKey);
                } else {
                    kvWriteBatch.delete(transactionHashKey);
                }
            }
        }
    }

    private void storeAddress(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] addressKey = BlockchainDatabaseKeyTool.buildAddressKey(output.getAddress());
                        if(BlockchainAction.ADD_BLOCK == blockchainAction){
                            kvWriteBatch.put(addressKey, addressKey);
                        } else {
                            kvWriteBatch.delete(addressKey);
                        }
                    }
                }
            }
        }
    }
    private void storeAddressToUnspentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return;
        }
        for(Transaction transaction : transactions){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspentTransactionOutput();
                    byte[] addressToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(utxo.getAddress());
                    if(blockchainAction == BlockchainAction.ADD_BLOCK){
                        kvWriteBatch.delete(addressToUnspentTransactionOutputHeightKey);
                    }else{
                        kvWriteBatch.put(addressToUnspentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(utxo.getTransactionOutputHeight()));
                    }
                }
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(transactionOutput.getAddress());
                    if(blockchainAction == BlockchainAction.ADD_BLOCK){
                        kvWriteBatch.put(addressToUnspentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(transactionOutput.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToUnspentTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    private void storeAddressToTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return;
        }
        for(Transaction transaction : transactions){
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToTransactionOutputHeightKey(transactionOutput.getAddress());
                    if(blockchainAction == BlockchainAction.ADD_BLOCK){
                        kvWriteBatch.put(addressToTransactionOutputHeightKey, ByteUtil.uint64ToBytes(transactionOutput.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    private void storeAddressToSpentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return;
        }
        for(Transaction transaction : transactions){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspentTransactionOutput();
                    byte[] addressToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToSpentTransactionOutputHeightKey(utxo.getAddress());
                    if(blockchainAction == BlockchainAction.ADD_BLOCK){
                        kvWriteBatch.put(addressToSpentTransactionOutputHeightKey, ByteUtil.uint64ToBytes(utxo.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToSpentTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    //endregion

    private String getBlockchainDatabasePath(){
        return FileUtil.newPath(coreConfiguration.getCorePath(), BLOCKCHAIN_DATABASE_NAME);
    }

    //region block hash related
    /**
     * check Block New Hash
     */
    private boolean checkBlockNewHash(Block block) {
        if(BlockTool.isExistDuplicateNewHash(block)){
            LogUtil.debug("The block data is abnormal, exist duplicate hash.");
            return false;
        }

        String blockHash = block.getHash();
        if(isHashUsed(blockHash)){
            LogUtil.debug("The block data is abnormal, and the block hash has been used.");
            return false;
        }

        List<Transaction> blockTransactions = block.getTransactions();
        if(blockTransactions != null){
            for(Transaction transaction:blockTransactions){
                if(!checkTransactionNewHash(transaction)){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkTransactionNewHash(Transaction transaction) {
        String transactionHash = transaction.getTransactionHash();
        if(isHashUsed(transactionHash)){
            return false;
        }
        return true;
    }
    private boolean isHashUsed(String hash){
        byte[] bytesHash = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildHashKey(hash));
        return bytesHash != null;
    }
    //endregion

    //region address related
    private boolean checkBlockNewAddress(Block block) {
        if(BlockTool.isExistDuplicateNewAddress(block)){
            return false;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!checkTransactionNewAddress(transaction)){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkTransactionNewAddress(Transaction transaction) {
        if(TransactionTool.isExistDuplicateNewAddress(transaction)){
            return false;
        }

        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                if(isAddressUsed(address)){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isAddressUsed(String address) {
        byte[] bytesAddress = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressKey(address));
        return bytesAddress != null;
    }
    //endregion


    //region Double spend attack
    private boolean checkBlockDoubleSpend(Block block) {
        if(BlockTool.isExistDuplicateUtxo(block)){
            LogUtil.debug("Abnormal block data: a double-spend transaction occurred.");
            return false;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!checkTransactionDoubleSpend(transaction)){
                    LogUtil.debug("Abnormal block data: a double-spend transaction occurred.");
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * check Transaction Double Spend
     */
    private boolean checkTransactionDoubleSpend(Transaction transaction) {
        //Double spend transaction: there is a duplicate [unspent transaction output] inside the transaction
        if(TransactionTool.isExistDuplicateUtxo(transaction)){
            LogUtil.debug("The transaction data is abnormal, and a double-spending attack is detected.");
            return false;
        }
        //Double spend transaction: transaction uses [spent [unspent transaction output]] inside the transaction
        if(!checkStxoIsUtxo(transaction)){
            LogUtil.debug("The transaction data is abnormal, and a double-spending attack is detected.");
            return false;
        }
        return true;
    }
    /**
     * Check if [spent transaction outputs] are all [unspent transaction outputs] ?
     */
    private boolean checkStxoIsUtxo(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                TransactionOutput transactionOutput = queryUnspentTransactionOutputByTransactionOutputId(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                if(transactionOutput == null){
                    LogUtil.debug("Transaction data exception: transaction input is not unspent transaction output.");
                    return false;
                }
            }
        }
        return true;
    }
    //endregion


    private boolean checkTransactionScript(Transaction transaction){
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    OutputScript outputScript = transactionInput.getUnspentTransactionOutput().getOutputScript();
                    InputScript inputScript = transactionInput.getInputScript();
                    Script script = ScriptTool.createScript(inputScript,outputScript);
                    Result result = virtualMachine.execute(transaction,script);
                    boolean executeSuccess = result.size()==1 && ByteUtil.equals(BooleanCode.TRUE.getCode(),ByteUtil.hexStringToBytes(result.pop()));
                    if(!executeSuccess){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            LogUtil.error("Transaction verification failed: transaction [input script] unlock transaction [output script] exception.",e);
            return false;
        }
        return true;
    }




    //region dto to model
    public Block blockDto2Block(BlockDto blockDto) {
        String previousBlockHash = blockDto.getPreviousHash();
        Block previousBlock = queryBlockByBlockHash(previousBlockHash);

        Block block = new Block();
        block.setTimestamp(blockDto.getTimestamp());
        block.setPreviousHash(previousBlockHash);
        block.setNonce(blockDto.getNonce());

        long blockHeight = BlockTool.getNextBlockHeight(previousBlock);
        block.setHeight(blockHeight);

        List<Transaction> transactions = transactionDtos2Transactions(blockDto.getTransactions());
        block.setTransactions(transactions);

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        block.setMerkleTreeRoot(merkleTreeRoot);

        String blockHash = BlockTool.calculateBlockHash(block);
        block.setHash(blockHash);

        String difficult = getConsensus().calculateDifficult(this,block);
        block.setDifficulty(difficult);

        fillBlockProperty(block);

        if(!getConsensus().checkConsensus(this,block)){
            throw new RuntimeException("Check Block Consensus Failed.");
        }
        return block;
    }
    private List<Transaction> transactionDtos2Transactions(List<TransactionDto> transactionDtos) {
        List<Transaction> transactions = new ArrayList<>();
        if(transactionDtos != null){
            for(TransactionDto transactionDto:transactionDtos){
                Transaction transaction = transactionDto2Transaction(transactionDto);
                transactions.add(transaction);
            }
        }
        return transactions;
    }
    public Transaction transactionDto2Transaction(TransactionDto transactionDto) {
        List<TransactionInput> inputs = new ArrayList<>();
        List<TransactionInputDto> transactionInputDtos = transactionDto.getInputs();
        if(transactionInputDtos != null){
            for (TransactionInputDto transactionInputDto:transactionInputDtos){
                TransactionOutput unspentTransactionOutput = queryUnspentTransactionOutputByTransactionOutputId(transactionInputDto.getTransactionHash(),transactionInputDto.getTransactionOutputIndex());
                if(unspentTransactionOutput == null){
                    throw new RuntimeException("Illegal transaction. the transaction input is not an unspent transaction output.");
                }
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspentTransactionOutput(unspentTransactionOutput);
                transactionInput.setInputScript(inputScriptDto2InputScript(transactionInputDto.getInputScript()));
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDto> transactionOutputDtos = transactionDto.getOutputs();
        if(transactionOutputDtos != null){
            for(TransactionOutputDto transactionOutputDto:transactionOutputDtos){
                TransactionOutput transactionOutput = transactionOutputDto2TransactionOutput(transactionOutputDto);
                outputs.add(transactionOutput);
            }
        }

        Transaction transaction = new Transaction();
        TransactionType transactionType = obtainTransactionDto(transactionDto);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
        transaction.setInputs(inputs);
        transaction.setOutputs(outputs);
        return transaction;
    }
    private TransactionOutput transactionOutputDto2TransactionOutput(TransactionOutputDto transactionOutputDto) {
        TransactionOutput transactionOutput = new TransactionOutput();
        String publicKeyHash = ScriptDtoTool.getPublicKeyHashFromPayToPublicKeyHashOutputScript(transactionOutputDto.getOutputScript());
        String address = AccountUtil.addressFromPublicKeyHash(publicKeyHash);
        transactionOutput.setAddress(address);
        transactionOutput.setValue(transactionOutputDto.getValue());
        transactionOutput.setOutputScript(outputScriptDto2OutputScript(transactionOutputDto.getOutputScript()));
        return transactionOutput;
    }
    private TransactionType obtainTransactionDto(TransactionDto transactionDto) {
        if(transactionDto.getInputs() == null || transactionDto.getInputs().size()==0){
            return TransactionType.COINBASE_TRANSACTION;
        }
        return TransactionType.STANDARD_TRANSACTION;
    }
    private OutputScript outputScriptDto2OutputScript(OutputScriptDto outputScriptDto) {
        if(outputScriptDto == null){
            return null;
        }
        OutputScript outputScript = new OutputScript();
        outputScript.addAll(outputScriptDto);
        return outputScript;
    }
    private InputScript inputScriptDto2InputScript(InputScriptDto inputScriptDto) {
        if(inputScriptDto == null){
            return null;
        }
        InputScript inputScript = new InputScript();
        inputScript.addAll(inputScriptDto);
        return inputScript;
    }

    private void fillBlockProperty(Block block) {
        long transactionIndex = 0;
        long transactionHeight = queryBlockchainTransactionHeight();
        long transactionOutputHeight = queryBlockchainTransactionOutputHeight();
        long blockHeight = block.getHeight();
        String blockHash = block.getHash();
        List<Transaction> transactions = block.getTransactions();
        long transactionCount = BlockTool.getTransactionCount(block);
        block.setTransactionCount(transactionCount);
        block.setPreviousTransactionHeight(transactionHeight);
        if(transactions != null){
            for(Transaction transaction:transactions){
                transactionIndex++;
                transactionHeight++;
                transaction.setBlockHeight(blockHeight);
                transaction.setTransactionIndex(transactionIndex);
                transaction.setTransactionHeight(transactionHeight);

                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (int i=0; i <outputs.size(); i++){
                        transactionOutputHeight++;
                        TransactionOutput output = outputs.get(i);
                        output.setBlockHeight(blockHeight);
                        output.setBlockHash(blockHash);
                        output.setTransactionHeight(transactionHeight);
                        output.setTransactionHash(transaction.getTransactionHash());
                        output.setTransactionOutputIndex(i+1);
                        output.setTransactionIndex(transaction.getTransactionIndex());
                        output.setTransactionOutputHeight(transactionOutputHeight);
                    }
                }
            }
        }
    }
    //endregion
}