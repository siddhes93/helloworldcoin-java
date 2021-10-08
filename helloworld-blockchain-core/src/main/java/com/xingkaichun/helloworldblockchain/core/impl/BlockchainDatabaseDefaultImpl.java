package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.enums.BlockchainAction;
import com.xingkaichun.helloworldblockchain.core.model.script.*;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.tool.*;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GenesisBlockSetting;
import com.xingkaichun.helloworldblockchain.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 区块链数据库
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainDatabaseDefaultImpl extends BlockchainDatabase {

    //region 变量与构造函数
    private static final String BLOCKCHAIN_DATABASE_NAME = "BlockchainDatabase";

    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BlockchainDatabaseDefaultImpl(CoreConfiguration coreConfiguration, Incentive incentive, Consensus consensus, VirtualMachine virtualMachine) {
        super(consensus,incentive,virtualMachine);
        this.coreConfiguration = coreConfiguration;
    }
    //endregion



    //region 区块增加与删除
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



    //region 校验区块、交易
    @Override
    public boolean checkBlock(Block block) {

        //校验区块的结构
        if(!StructureTool.checkBlockStructure(block)){
            LogUtil.debug("区块数据异常，请校验区块的结构。");
            return false;
        }
        //校验区块的大小
        if(!SizeTool.checkBlockSize(block)){
            LogUtil.debug("区块数据异常，请校验区块的大小。");
            return false;
        }


        //校验业务
        Block previousBlock = queryTailBlock();
        //校验区块高度的连贯性
        if(!BlockTool.checkBlockHeight(previousBlock,block)){
            LogUtil.debug("区块写入的区块高度出错。");
            return false;
        }
        //校验区块的前区块哈希
        if(!BlockTool.checkPreviousBlockHash(previousBlock,block)){
            LogUtil.debug("区块写入的前区块哈希出错。");
            return false;
        }
        //校验区块时间
        if(!BlockTool.checkBlockTimestamp(previousBlock,block)){
            LogUtil.debug("区块生成的时间太滞后。");
            return false;
        }
        //校验新产生的哈希
        if(!checkBlockNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验新产生的地址
        if(!checkBlockNewAddress(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验双花
        if(!checkBlockDoubleSpend(block)){
            LogUtil.debug("区块数据异常，检测到双花攻击。");
            return false;
        }
        //校验共识
        if(!consensus.checkConsensus(this,block)){
            LogUtil.debug("区块数据异常，未满足共识规则。");
            return false;
        }
        //校验激励
        if(!incentive.checkIncentive(this,block)){
            LogUtil.debug("区块数据异常，激励校验失败。");
            return false;
        }

        //从交易角度校验每一笔交易
        for(Transaction transaction : block.getTransactions()){
            boolean transactionCanAddToNextBlock = checkTransaction(transaction);
            if(!transactionCanAddToNextBlock){
                LogUtil.debug("区块数据异常，交易异常。");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkTransaction(Transaction transaction) {
        //校验交易的结构
        if(!StructureTool.checkTransactionStructure(transaction)){
            LogUtil.debug("交易数据异常，请校验交易的结构。");
            return false;
        }
        //校验交易的大小
        if(!SizeTool.checkTransactionSize(transaction)){
            LogUtil.debug("交易数据异常，请校验交易的大小。");
            return false;
        }


        //校验交易中的地址是否是P2PKH地址
        if(!TransactionTool.checkPayToPublicKeyHashAddress(transaction)){
            return false;
        }
        //校验交易中的脚本是否是P2PKH脚本
        if(!TransactionTool.checkPayToPublicKeyHashScript(transaction)){
            return false;
        }

        //业务校验
        //校验新产生的哈希
        if(!checkTransactionNewHash(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验新产生的地址
        if(!checkTransactionNewAddress(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验交易金额
        if(!TransactionTool.checkTransactionValue(transaction)){
            LogUtil.debug("交易金额不合法");
            return false;
        }
        //校验双花
        if(!checkTransactionDoubleSpend(transaction)){
            LogUtil.debug("交易数据异常，检测到双花攻击。");
            return false;
        }
        //校验脚本
        if(!checkTransactionScript(transaction)) {
            LogUtil.debug("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。");
            return false;
        }
        return true;
    }
    //endregion



    //region 普通查询
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



    //region 区块查询
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



    //region 交易查询
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



    //region 交易输出查询
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



    //region 拼装WriteBatch
    /**
     * 根据区块信息组装WriteBatch对象
     */
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

        //TODO 将地址改为交易输出脚本
        storeAddressToTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeAddressToUnspentTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        storeAddressToSpentTransactionOutputHeight(kvWriteBatch,block, blockchainAction);
        return kvWriteBatch;
    }


    /**
     * [交易输出ID]到[来源交易高度]的映射
     */
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
    /**
     * [已花费交易输出ID]到[去向交易高度]的映射
     */
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
    /**
     * [交易输出ID]到[交易输出]的映射
     */
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
    /**
     * [交易输出高度]到[交易输出]的映射
     */
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
    /**
     * 存储未花费交易输出ID到未花费交易输出的映射
     */
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
    /**
     * 存储已花费交易输出ID到已花费交易输出的映射
     */
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
    /**
     * 存储交易高度到交易的映射
     */
    private void storeTransactionHeightToTransaction(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                //更新区块链中的交易序列号数据
                byte[] transactionHeightToTransactionKey = BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transaction.getTransactionHeight());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHeightToTransactionKey, EncodeDecodeTool.encode(transaction));
                } else {
                    kvWriteBatch.delete(transactionHeightToTransactionKey);
                }
            }
        }
    }
    /**
     * 存储交易哈希到交易高度的映射
     */
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
    /**
     * 存储区块链的高度
     */
    private void storeBlockchainHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockchainHeightKey = BlockchainDatabaseKeyTool.buildBlockchainHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.uint64ToBytes(block.getHeight()));
        }else{
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.uint64ToBytes(block.getHeight()-1));
        }
    }
    /**
     * 存储区块哈希到区块高度的映射
     */
    private void storeBlockHashToBlockHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHashBlockHeightKey = BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(block.getHash());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHashBlockHeightKey, ByteUtil.uint64ToBytes(block.getHeight()));
        }else{
            kvWriteBatch.delete(blockHashBlockHeightKey);
        }
    }
    /**
     * 存储区块链中总的交易高度
     */
    private void storeBlockchainTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        long transactionCount = queryBlockchainTransactionHeight();
        byte[] bytesBlockchainTransactionCountKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.uint64ToBytes(transactionCount + BlockTool.getTransactionCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.uint64ToBytes(transactionCount - BlockTool.getTransactionCount(block)));
        }
    }
    /**
     * 存储区块链中总的交易数量
     */
    private void storeBlockchainTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        long transactionOutputCount = queryBlockchainTransactionOutputHeight();
        byte[] bytesBlockchainTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.uint64ToBytes(transactionOutputCount + BlockTool.getTransactionOutputCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.uint64ToBytes(transactionOutputCount - BlockTool.getTransactionOutputCount(block)));
        }
    }
    /**
     * 存储区块链高度到区块的映射
     */
    private void storeBlockHeightToBlock(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHeightKey = BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(block.getHeight());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHeightKey, EncodeDecodeTool.encode(block));
        }else{
            kvWriteBatch.delete(blockHeightKey);
        }
    }

    /**
     * 存储已使用的哈希
     */
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

    /**
     * 存储已使用的地址
     */
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
    /**
     * 存储地址到未花费交易输出列表
     */
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
    /**
     * 存储地址到交易输出
     */
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
    /**
     * 存储地址到交易输出高度
     */
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

    //region 新产生的哈希相关
    /**
     * 校验区块新产生的哈希
     */
    private boolean checkBlockNewHash(Block block) {
        //校验哈希作为主键的正确性
        //新产生的哈希不能有重复
        if(BlockTool.isExistDuplicateNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希有重复。");
            return false;
        }

        //新产生的哈希不能被区块链使用过了
        //校验区块Hash是否已经被使用了
        String blockHash = block.getHash();
        if(isHashUsed(blockHash)){
            LogUtil.debug("区块数据异常，区块Hash已经被使用了。");
            return false;
        }
        //校验每一笔交易新产生的Hash是否正确
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
    /**
     * 区块中校验新产生的哈希
     */
    private boolean checkTransactionNewHash(Transaction transaction) {
        //校验哈希作为主键的正确性
        //校验交易Hash是否已经被使用了
        String transactionHash = transaction.getTransactionHash();
        if(isHashUsed(transactionHash)){
            LogUtil.debug("交易数据异常，交易Hash已经被使用了。");
            return false;
        }
        return true;
    }
    /**
     * 哈希是否已经被区块链系统使用了？
     */
    private boolean isHashUsed(String hash){
        byte[] bytesHash = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildHashKey(hash));
        return bytesHash != null;
    }
    //endregion

    //region 新产生的地址相关
    /**
     * 校验区块新产生的地址
     */
    private boolean checkBlockNewAddress(Block block) {
        //校验地址作为主键的正确性
        //新产生的地址不能有重复
        if(BlockTool.isExistDuplicateNewAddress(block)){
            LogUtil.debug("区块数据异常，区块中新产生的地址有重复。");
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
    /**
     * 区块中校验新产生的哈希
     */
    private boolean checkTransactionNewAddress(Transaction transaction) {
        //区块新产生的地址不能有重复
        if(TransactionTool.isExistDuplicateNewAddress(transaction)){
            return false;
        }
        //区块新产生的地址不能被使用过了
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                if(isAddressUsed(address)){
                    LogUtil.debug("区块数据异常，地址["+address+"]重复。");
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


    //region 双花攻击
    /**
     * 校验双花
     * 双花指的是同一笔UTXO被花费两次或多次。
     */
    private boolean checkBlockDoubleSpend(Block block) {
        //双花交易：区块内部存在重复的[未花费交易输出]
        if(BlockTool.isExistDuplicateUtxo(block)){
            LogUtil.debug("区块数据异常：发生双花交易。");
            return false;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!checkTransactionDoubleSpend(transaction)){
                    LogUtil.debug("区块数据异常：发生双花交易。");
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 校验双花
     */
    private boolean checkTransactionDoubleSpend(Transaction transaction) {
        //双花交易：交易内部存在重复的[未花费交易输出]
        if(TransactionTool.isExistDuplicateUtxo(transaction)){
            LogUtil.debug("交易数据异常，检测到双花攻击。");
            return false;
        }
        //双花交易：交易内部使用了[已经花费的[未花费交易输出]]
        if(!checkStxoIsUtxo(transaction)){
            LogUtil.debug("交易数据异常：发生双花交易。");
            return false;
        }
        return true;
    }
    /**
     * 检查[花费的交易输出]是否都是[未花费的交易输出]
     */
    private boolean checkStxoIsUtxo(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                TransactionOutput transactionOutput = queryUnspentTransactionOutputByTransactionOutputId(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                if(transactionOutput == null){
                    LogUtil.debug("交易数据异常：交易输入不是未花费交易输出。");
                    return false;
                }
            }
        }
        return true;
    }
    //endregion


    /**
     * 检验交易脚本，即校验交易输入能解锁交易输出吗？即用户花费的是自己的钱吗？
     * 校验用户花费的是自己的钱吗，用户只可以花费自己的钱。专业点的说法，校验UTXO所有权，用户只可以花费自己拥有的UTXO。
     * 用户如何能证明自己拥有这个UTXO，只要用户能创建出一个能解锁(该UTXO对应的交易输出脚本)的交易输入脚本，就证明了用户拥有该UTXO。
     * 这是因为锁(交易输出脚本)是用户创建的，自然只有该用户有对应的钥匙(交易输入脚本)，自然意味着有钥匙的用户拥有这把锁的所有权。
     */
    private boolean checkTransactionScript(Transaction transaction){
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    //锁(交易输出脚本)
                    OutputScript outputScript = transactionInput.getUnspentTransactionOutput().getOutputScript();
                    //钥匙(交易输入脚本)
                    InputScript inputScript = transactionInput.getInputScript();
                    //完整脚本
                    Script script = ScriptTool.createScript(inputScript,outputScript);
                    //执行脚本
                    Result result = virtualMachine.execute(transaction,script);
                    //脚本执行结果是个栈，如果栈有且只有一个元素，且这个元素是0x01，则解锁成功。
                    boolean executeSuccess = result.size()==1 && ByteUtil.equals(BooleanCode.TRUE.getCode(),ByteUtil.hexStringToBytes(result.pop()));
                    if(!executeSuccess){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            LogUtil.error("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。",e);
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
        /*
         * 预先校验区块工作量共识。伪造工作量共识是一件十分耗费资源的事情，因此预先校验工作量共识可以抵消绝大部分的攻击。
         * 也可以选择跳过此处预检，后续业务有完整的校验检测。
         * 此处预检，只是想预先抵消绝大部分的攻击。
         */
        if(!getConsensus().checkConsensus(this,block)){
            throw new RuntimeException("区块预检失败。");
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
                    throw new RuntimeException("非法交易。交易输入并不是一笔未花费交易输出。");
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
            return TransactionType.GENESIS_TRANSACTION;
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
    /**
     * 补充区块的属性
     */
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