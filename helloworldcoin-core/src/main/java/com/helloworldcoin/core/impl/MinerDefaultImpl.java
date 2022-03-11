package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.*;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionInput;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.transaction.TransactionType;
import com.helloworldcoin.core.tool.*;
import com.helloworldcoin.crypto.model.Account;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.setting.BlockSetting;
import com.helloworldcoin.setting.GenesisBlockSetting;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;
import com.helloworldcoin.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class MinerDefaultImpl extends Miner {

    //region
    public MinerDefaultImpl(CoreConfiguration coreConfiguration, Wallet wallet, BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {
        this.coreConfiguration = coreConfiguration;
        this.wallet = wallet;
        this.blockchainDatabase = blockchainDatabase;
        this.unconfirmedTransactionDatabase = unconfirmedTransactionDatabase;
    }
    //endregion


    @Override
    public void start() {
        while (true){
            ThreadUtil.millisecondSleep(10);
            if(!isActive()){
                continue;
            }
            if(isMiningHeightExceedsLimit()){
                continue;
            }

            Account minerAccount = wallet.createAccount();
            Block block = buildMiningBlock(blockchainDatabase,unconfirmedTransactionDatabase,minerAccount);
            long startTimestamp = TimeUtil.millisecondTimestamp();
            while (true){
                if(!isActive()){
                    break;
                }
                if(TimeUtil.millisecondTimestamp()-startTimestamp > coreConfiguration.getMinerMineTimeInterval()){
                    break;
                }
                block.setNonce(ByteUtil.bytesToHexString(ByteUtil.random32Bytes()));
                block.setHash(BlockTool.calculateBlockHash(block));
                if(blockchainDatabase.getConsensus().checkConsensus(blockchainDatabase,block)){
                    wallet.saveAccount(minerAccount);
                    LogUtil.debug("Congratulations! Mining success! Block height:"+block.getHeight()+", Block hash:"+block.getHash());
                    BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
                    boolean isAddBlockToBlockchainSuccess = blockchainDatabase.addBlockDto(blockDto);
                    if(!isAddBlockToBlockchainSuccess){
                        LogUtil.debug("Mining succeeded, but the block failed to be put into the blockchain.");
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void deactive() {
        coreConfiguration.deactiveMiner();
    }

    @Override
    public void active() {
        coreConfiguration.activeMiner();
    }

    @Override
    public boolean isActive() {
        return coreConfiguration.isMinerActive();
    }


    @Override
    public void setMinerMineMaxBlockHeight(long maxHeight) {
         coreConfiguration.setMinerMineMaxBlockHeight(maxHeight);
    }

    @Override
    public long getMinerMineMaxBlockHeight( ) {
        return coreConfiguration.getMinerMineMaxBlockHeight();
    }

    private Block buildMiningBlock(BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase, Account minerAccount) {
        long timestamp = TimeUtil.millisecondTimestamp();

        Block tailBlock = blockchainDatabase.queryTailBlock();
        Block nonNonceBlock = new Block();
        nonNonceBlock.setTimestamp(timestamp);

        if(tailBlock == null){
            nonNonceBlock.setHeight(GenesisBlockSetting.HEIGHT +1);
            nonNonceBlock.setPreviousHash(GenesisBlockSetting.HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight()+1);
            nonNonceBlock.setPreviousHash(tailBlock.getHash());
        }
        List<Transaction> packingTransactions = packingTransactions(blockchainDatabase,unconfirmedTransactionDatabase);
        nonNonceBlock.setTransactions(packingTransactions);

        Incentive incentive = blockchainDatabase.getIncentive();
        long incentiveValue = incentive.incentiveValue(blockchainDatabase,nonNonceBlock);

        Transaction mineAwardTransaction = buildIncentiveTransaction(minerAccount.getAddress(),incentiveValue);
        nonNonceBlock.getTransactions().add(0,mineAwardTransaction);

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(nonNonceBlock);
        nonNonceBlock.setMerkleTreeRoot(merkleTreeRoot);

        nonNonceBlock.setDifficulty(blockchainDatabase.getConsensus().calculateDifficult(blockchainDatabase,nonNonceBlock));
        return nonNonceBlock;
    }

    private Transaction buildIncentiveTransaction(String address,long incentiveValue) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.COINBASE_TRANSACTION);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        TransactionOutput output = new TransactionOutput();
        output.setAddress(address);
        output.setValue(incentiveValue);
        output.setOutputScript(ScriptTool.createPayToPublicKeyHashOutputScript(address));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }

    private List<Transaction> packingTransactions(BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {
        List<TransactionDto> forMineBlockTransactionDtos = unconfirmedTransactionDatabase.selectTransactions(1,10000);

        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> backupTransactions = new ArrayList<>();

        if(forMineBlockTransactionDtos != null){
            for(TransactionDto transactionDto:forMineBlockTransactionDtos){
                try {
                    Transaction transaction = blockchainDatabase.transactionDto2Transaction(transactionDto);
                    transactions.add(transaction);
                } catch (Exception e) {
                    String transactionHash = TransactionDtoTool.calculateTransactionHash(transactionDto);
                    LogUtil.error("Abnormal transaction, transaction hash:"+transactionHash,e);
                    unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
                }
            }
        }

        backupTransactions.clear();
        backupTransactions.addAll(transactions);
        transactions.clear();
        for(Transaction transaction : backupTransactions){
            boolean checkTransaction = blockchainDatabase.checkTransaction(transaction);
            if(checkTransaction){
                transactions.add(transaction);
            }else {
                String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                LogUtil.debug("Abnormal transaction, transaction hash:"+transactionHash);
                unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
            }
        }

        backupTransactions.clear();
        backupTransactions.addAll(transactions);
        transactions.clear();

        //prevent double spending
        Set<String> transactionOutputIdSet = new HashSet<>();
        for(Transaction transaction : backupTransactions){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                boolean canAdd = true;
                for(TransactionInput transactionInput : inputs) {
                    TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                    String transactionOutputId = TransactionTool.getTransactionOutputId(unspentTransactionOutput);
                    if(transactionOutputIdSet.contains(transactionOutputId)){
                        canAdd = false;
                        break;
                    }else {
                        transactionOutputIdSet.add(transactionOutputId);
                    }
                }
                if(canAdd){
                    transactions.add(transaction);
                }else {
                    String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                    LogUtil.debug("Abnormal transaction, transaction hash:"+transactionHash);
                    unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
                }
            }
        }



        backupTransactions.clear();
        backupTransactions.addAll(transactions);
        transactions.clear();

        //Prevent an address used multiple times
        Set<String> addressSet = new HashSet<>();
        for(Transaction transaction : backupTransactions){
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                boolean canAdd = true;
                for (TransactionOutput output:outputs){
                    String address = output.getAddress();
                    if(addressSet.contains(address)){
                        canAdd = false;
                        break;
                    }else {
                        addressSet.add(address);
                    }
                }
                if(canAdd){
                    transactions.add(transaction);
                }else {
                    String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                    LogUtil.debug("Abnormal transaction, transaction hash:"+transactionHash);
                    unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
                }
            }
        }


        TransactionTool.sortByTransactionFeeRateDescend(transactions);


        backupTransactions.clear();
        backupTransactions.addAll(transactions);
        transactions.clear();

        long size = 0;
        for(int i=0; i<backupTransactions.size(); i++){
            if(i+1 > BlockSetting.BLOCK_MAX_TRANSACTION_COUNT-1){
                break;
            }
            Transaction transaction = backupTransactions.get(i);
            size += SizeTool.calculateTransactionSize(transaction);
            if(size > BlockSetting.BLOCK_MAX_CHARACTER_COUNT){
                break;
            }
            transactions.add(transaction);
        }
        return transactions;
    }

    private Boolean isMiningHeightExceedsLimit(){
        long blockChainHeight = blockchainDatabase.queryBlockchainHeight();
        return blockChainHeight >= coreConfiguration.getMinerMineMaxBlockHeight();
    }

}
