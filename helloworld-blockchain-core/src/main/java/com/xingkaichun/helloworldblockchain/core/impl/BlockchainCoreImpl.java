package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.pay.Recipient;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.UnspendTransactionOutputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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



    public BuildTransactionResponse buildTransactionDTO(BuildTransactionRequest request) {
        List<Account> allAccountList = wallet.queryAllAccount();
        if(allAccountList == null || allAccountList.isEmpty()){
            BuildTransactionResponse response = new BuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("钱包中的余额不足支付。");
            return response;
        }
        List<String> privateKeyList = new ArrayList<>();
        for(Account account:allAccountList){
            privateKeyList.add(account.getPrivateKey());
        }
        //存放找零
        Account account = allAccountList.get(0);
        return buildTransactionDTO(privateKeyList,account.getAddress(),request.getRecipientList());
    }

    public BuildTransactionResponse buildTransactionDTO(List<String> payerPrivateKeyList,String payerChangeAddress,List<Recipient> recipientList) {
        //理应支付总金额
        long outputValues = 0;
        if(recipientList != null){
            for(Recipient recipient : recipientList){
                outputValues += recipient.getValue();
            }
        }
        //创建交易输出
        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        if(recipientList != null){
            for(Recipient recipient : recipientList){
                TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
                transactionOutputDTO.setValue(recipient.getValue());
                OutputScript outputScript = StackBasedVirtualMachine.createPayToPublicKeyHashOutputScript(recipient.getAddress());
                transactionOutputDTO.setOutputScriptDTO(Model2DtoTool.outputScript2OutputScriptDTO(outputScript));
                transactionOutputDtoList.add(transactionOutputDTO);
            }
        }

        //获取足够的金额
        //交易输入列表
        List<TransactionOutput> inputs = new ArrayList<>();
        List<String> inputPrivateKeyList = new ArrayList<>();
        //交易输入总金额
        long inputValues = 0;
        long feeValues = 0;
        boolean haveEnoughMoneyToPay = false;
        for(String privateKey : payerPrivateKeyList){
            if(haveEnoughMoneyToPay){
                break;
            }
            String address = AccountUtil.accountFromPrivateKey(privateKey).getAddress();
            long from = 0;
            long size = 100;
            while (true){
                if(haveEnoughMoneyToPay){
                    break;
                }
                List<TransactionOutput> utxoList = blockchainDataBase.queryUnspendTransactionOutputListByAddress(address,from,size);
                if(utxoList == null || utxoList.isEmpty()){
                    break;
                }
                for(TransactionOutput transactionOutput:utxoList){
                    inputValues += transactionOutput.getValue();
                    //交易输入
                    inputs.add(transactionOutput);
                    inputPrivateKeyList.add(privateKey);
                    //大于的一部分可以用于交易手续费
                    if(inputValues > outputValues){
                        haveEnoughMoneyToPay = true;
                        break;
                    }
                }
                from += size;
            }
        }

        if(!haveEnoughMoneyToPay){
            BuildTransactionResponse buildTransactionResponse = new BuildTransactionResponse();
            buildTransactionResponse.setBuildTransactionSuccess(false);
            buildTransactionResponse.setMessage("账户没有足够的金额去支付");
            return buildTransactionResponse;
        }

        //构建交易输入
        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        for(TransactionOutput input:inputs){
            UnspendTransactionOutputDTO unspendTransactionOutputDto = Model2DtoTool.transactionOutput2UnspendTransactionOutputDto(input);
            TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
            transactionInputDTO.setUnspendTransactionOutputDTO(unspendTransactionOutputDto);
            transactionInputDtoList.add(transactionInputDTO);
        }

        //构建交易
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionInputDtoList(transactionInputDtoList);
        transactionDTO.setTransactionOutputDtoList(transactionOutputDtoList);

        //找零
        long change = inputValues - outputValues - feeValues;
        if(change > 0){
            TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
            transactionOutputDTO.setValue(change);
            OutputScript outputScript = StackBasedVirtualMachine.createPayToPublicKeyHashOutputScript(payerChangeAddress);
            transactionOutputDTO.setOutputScriptDTO(Model2DtoTool.outputScript2OutputScriptDTO(outputScript));
            transactionOutputDtoList.add(transactionOutputDTO);
        }

        //签名
        for(int i=0;i<transactionInputDtoList.size();i++){
            String privateKey = inputPrivateKeyList.get(i);
            String publicKey = AccountUtil.accountFromPrivateKey(privateKey).getPublicKey();
            TransactionInputDTO transactionInputDTO = transactionInputDtoList.get(i);
            String signature = Model2DtoTool.signature(transactionDTO,privateKey);
            InputScript inputScript = StackBasedVirtualMachine.createPayToPublicKeyHashInputScript(signature, publicKey);
            transactionInputDTO.setInputScriptDTO(Model2DtoTool.inputScript2InputScriptDTO(inputScript));
        }


        BuildTransactionResponse buildTransactionResponse = new BuildTransactionResponse();
        buildTransactionResponse.setBuildTransactionSuccess(true);
        buildTransactionResponse.setMessage("构建交易成功");
        buildTransactionResponse.setTransactionHash(TransactionTool.calculateTransactionHash(transactionDTO));
        buildTransactionResponse.setFee(feeValues);
        if(change > 0){
            buildTransactionResponse.setPayerChangeAddress(payerChangeAddress);
            buildTransactionResponse.setPayerChangeValue(change);
        }
        buildTransactionResponse.setTransactionInputList(inputs);
        buildTransactionResponse.setTransactionOutputDtoList(transactionOutputDtoList);
        buildTransactionResponse.setTransactionDTO(transactionDTO);
        return buildTransactionResponse;
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