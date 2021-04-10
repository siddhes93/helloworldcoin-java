package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Miner;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.pay.Recipient;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.tools.WalletTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 默认实现
 * 
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainCoreImpl extends BlockchainCore {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainCoreImpl.class);

    public BlockchainCoreImpl(BlockchainDatabase blockchainDataBase, Wallet wallet, Miner miner) {
        super(blockchainDataBase,wallet,miner);
    }

    @Override
    public void start() {
        //启动矿工线程
        new Thread(
                ()->{
                    try {
                        miner.start();
                    } catch (Exception e) {
                        logger.error("矿工在运行中发生异常并退出，请检查修复异常！",e);
                    }
                }
        ).start();
    }








    @Override
    public String queryBlockHashByBlockHeight(long blockHeight) {
        Block block = blockchainDataBase.queryBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        return block.getHash();
    }

    @Override
    public long queryBlockchainHeight() {
        return blockchainDataBase.queryBlockchainHeight();
    }







    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        Transaction transaction = blockchainDataBase.queryTransactionByTransactionHash(transactionHash);
        return transaction;
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        Transaction  transaction = blockchainDataBase.queryTransactionByTransactionHeight(transactionHeight);
        return transaction;
    }

    @Override
    public List<Transaction> queryTransactionListByTransactionHeight(long from,long size) {
        List<Transaction>  transactionList = blockchainDataBase.queryTransactionListByTransactionHeight(from,size);
        return transactionList;
    }

    @Override
    public List<Transaction> queryTransactionListByAddress(String address,long from,long size) {
        List<Transaction>  transactionList = blockchainDataBase.queryTransactionListByAddress(address,from,size);
        return transactionList;
    }

    @Override
    public List<TransactionOutput> queryTransactionOutputListByAddress(String address,long from,long size) {
        List<TransactionOutput> txo =  blockchainDataBase.queryTransactionOutputListByAddress(address,from,size);
        return txo;
    }

    @Override
    public List<TransactionOutput> queryUnspendTransactionOutputListByAddress(String address, long from, long size) {
        List<TransactionOutput> utxo =  blockchainDataBase.queryUnspendTransactionOutputListByAddress(address,from,size);
        return utxo;
    }

    @Override
    public List<TransactionOutput> querySpendTransactionOutputListByAddress(String address, long from, long size) {
        List<TransactionOutput> stxo =  blockchainDataBase.querySpendTransactionOutputListByAddress(address,from,size);
        return stxo;
    }









    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        return blockchainDataBase.queryBlockByBlockHeight(blockHeight);
    }

    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        return blockchainDataBase.queryBlockByBlockHash(blockHash);
    }

    @Override
    public Block queryTailBlock() {
        return blockchainDataBase.queryTailBlock();
    }

    @Override
    public void deleteTailBlock() {
        blockchainDataBase.deleteTailBlock();
    }

    @Override
    public boolean addBlock(Block block) {
        return blockchainDataBase.addBlock(block);
    }


    @Override
    public void deleteBlocks(long blockHeight) {
        blockchainDataBase.deleteBlocks(blockHeight);
    }



    public BuildTransactionResponse buildTransactionDTO2(BuildTransactionRequest request) {
        List<String> payerPrivateKeyList = request.getPayerPrivateKeyList();
        if(payerPrivateKeyList == null || payerPrivateKeyList.size()==0){
            BuildTransactionResponse response = new BuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("请输入交易输入");
            return response;
        }
        List<Recipient> recipientList = request.getRecipientList();
        String payerChangeAddress = request.getPayerChangeAddress();
        if(StringUtil.isNullOrEmpty(payerChangeAddress)){
            BuildTransactionResponse response = new BuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("请输入找零地址");
            return response;
        }
        long fee = request.getFee();
        if(fee < 0){
            BuildTransactionResponse response = new BuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("交易手续费太少");
            return response;
        }
        BuildTransactionResponse response = buildTransactionDTO(payerPrivateKeyList,recipientList,payerChangeAddress,fee);
        return response;
    }

    public BuildTransactionResponse buildTransactionDTO(BuildTransactionRequest request) {
        List<Account> allAccountList = wallet.queryAllAccount();
        if(allAccountList == null || allAccountList.isEmpty()){
            BuildTransactionResponse response = new BuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("钱包中的余额不足支付。");
            return response;
        }

        BuildTransactionResponse response = new BuildTransactionResponse();
        response.setMessage("请输入足够的金额");
        response.setBuildTransactionSuccess(false);

        //创建一个地址用于存放找零
        Account payerChangeAccount = wallet.createAccount();
        wallet.addAccount(payerChangeAccount);

        List<String> privateKeyList = new ArrayList<>();
        for(Account account:allAccountList){
            privateKeyList.add(account.getPrivateKey());
            response = buildTransactionDTO(privateKeyList,request.getRecipientList(),payerChangeAccount.getAddress(),0);
            if(response.isBuildTransactionSuccess()){
                return response;
            }
        }
        return response;
    }
    public BuildTransactionResponse buildTransactionDTO(List<String> payerPrivateKeyList, List<Recipient> recipientList, String payerChangeAddress, long fee) {
        LinkedHashMap<String,List<TransactionOutput>> privateKeyUtxoMap = new LinkedHashMap<>();
        BuildTransactionResponse response = new BuildTransactionResponse();
        response.setMessage("请输入足够的金额");
        response.setBuildTransactionSuccess(false);

        for(String privateKey : payerPrivateKeyList){
            if(response.isBuildTransactionSuccess()){
                break;
            }
            String address = AccountUtil.accountFromPrivateKey(privateKey).getAddress();
            long from = 0;
            long size = 100;
            while (true){
                List<TransactionOutput> utxoList = blockchainDataBase.queryUnspendTransactionOutputListByAddress(address,from,size);
                if(utxoList == null || utxoList.isEmpty()){
                    break;
                }
                List<TransactionOutput> payUtxoList = privateKeyUtxoMap.get(privateKey);
                if(payUtxoList == null){
                    privateKeyUtxoMap.put(privateKey,new ArrayList<>());
                }
                privateKeyUtxoMap.get(privateKey).addAll(utxoList);
                from += size;

                response = WalletTool.buildTransactionDTO(privateKeyUtxoMap,recipientList,payerChangeAddress,fee);
                if(response.isBuildTransactionSuccess()){
                    break;
                }
            }
        }
        return response;
    }

    @Override
    public void submitTransaction(TransactionDTO transactionDTO) {
        miner.getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
    }

    @Override
    public List<TransactionDTO> queryMiningTransactionList(long from,long size) {
        List<TransactionDTO> transactionDtoList = miner.getMinerTransactionDtoDataBase().selectTransactionDtoList(from,size);
        return transactionDtoList;
    }

    @Override
    public TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) {
        TransactionDTO transactionDTO = miner.getMinerTransactionDtoDataBase().selectTransactionDtoByTransactionHash(transactionHash);
        return transactionDTO;
    }
}