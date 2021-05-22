package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.*;
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
import com.xingkaichun.helloworldblockchain.util.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainCoreImpl extends BlockchainCore {

    public BlockchainCoreImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDataBase, Wallet wallet, Miner miner) {
        super(coreConfiguration,blockchainDataBase,wallet,miner);
    }

    @Override
    public void start() {
        //启动矿工线程
        new Thread(
                ()->{
                    try {
                        miner.start();
                    } catch (Exception e) {
                        SystemUtil.errorExit("矿工在运行中发生异常，请检查修复异常！",e);
                    }
                }
        ).start();
    }

    @Override
    public long queryBlockchainHeight() {
        return blockchainDataBase.queryBlockchainHeight();
    }


    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        return blockchainDataBase.queryTransactionByTransactionHash(transactionHash);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        return blockchainDataBase.queryTransactionByTransactionHeight(transactionHeight);
    }

    @Override
    public TransactionOutput queryTransactionOutputByAddress(String address) {
        return blockchainDataBase.queryTransactionOutputByAddress(address);
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



    @Override
    public BuildTransactionResponse buildTransactionDTO(BuildTransactionRequest request) {
        List<Account> allAccountList = wallet.getAllAccount();
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
            //TODO
            response = buildTransactionDTO(privateKeyList,request.getRecipientList(),payerChangeAccount.getAddress(),100);
            if(response.isBuildTransactionSuccess()){
                return response;
            }
        }
        return response;
    }
    public BuildTransactionResponse buildTransactionDTO(List<String> payerPrivateKeyList, List<Recipient> recipientList, String payerChangeAddress, long fee) {
        Map<String,TransactionOutput> privateKeyUtxoMap = new HashMap<>();
        BuildTransactionResponse response = new BuildTransactionResponse();
        response.setMessage("请输入足够的金额");
        response.setBuildTransactionSuccess(false);

        for(String privateKey : payerPrivateKeyList){
            String address = AccountUtil.accountFromPrivateKey(privateKey).getAddress();
            TransactionOutput utxo = blockchainDataBase.queryUnspentTransactionOutputByAddress(address);
            if(utxo == null || utxo.getValue() <= 0){
                continue;
            }
            privateKeyUtxoMap.put(privateKey,utxo);
            response = WalletTool.buildTransactionDTO(privateKeyUtxoMap,recipientList,payerChangeAddress,fee);
            if(response.isBuildTransactionSuccess()){
                break;
            }
        }
        return response;
    }

    @Override
    public void submitTransaction(TransactionDTO transactionDTO) {
        miner.getUnconfirmedTransactionDataBase().insertTransaction(transactionDTO);
    }

    @Override
    public List<TransactionDTO> queryMiningTransactionList(long from,long size) {
        return miner.getUnconfirmedTransactionDataBase().selectTransactionList(from,size);
    }

    @Override
    public TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) {
        return miner.getUnconfirmedTransactionDataBase().selectTransactionByTransactionHash(transactionHash);
    }
}