package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.wallet.*;
import com.xingkaichun.helloworldblockchain.core.tool.ScriptDtoTool;
import com.xingkaichun.helloworldblockchain.core.tool.TransactionDtoTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class WalletImpl extends Wallet {

    private static final String WALLET_DATABASE_NAME = "WalletDatabase";

    public WalletImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        //获取所有
        List<byte[]> bytesAccounts = KvDbUtil.gets(getWalletDatabasePath(),1,100000000);
        if(bytesAccounts != null){
            for(byte[] bytesAccount:bytesAccounts){
                Account account = EncodeDecodeTool.decode(bytesAccount,Account.class);
                accounts.add(account);
            }
        }
        return accounts;
    }

    @Override
    public List<Account> getNonZeroBalanceAccounts() {
        List<Account> accounts = new ArrayList<>();
        //获取所有
        List<byte[]> bytesAccounts = KvDbUtil.gets(getWalletDatabasePath(),1,100000000);
        if(bytesAccounts != null){
            for(byte[] bytesAccount:bytesAccounts){
                Account account = EncodeDecodeTool.decode(bytesAccount,Account.class);
                TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(account.getAddress());
                if(utxo != null && utxo.getValue() > 0){
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    @Override
    public Account createAccount() {
        return AccountUtil.randomAccount();
    }

    @Override
    public Account createAndSaveAccount() {
        Account account = createAccount();
        saveAccount(account);
        return account;
    }

    @Override
    public void saveAccount(Account account) {
        KvDbUtil.put(getWalletDatabasePath(),getKeyByAccount(account), EncodeDecodeTool.encode(account));
    }

    @Override
    public void deleteAccountByAddress(String address) {
        KvDbUtil.delete(getWalletDatabasePath(),getKeyByAddress(address));
    }

    @Override
    public long getBalanceByAddress(String address) {
        TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(address);
        if(utxo != null){
            return utxo.getValue();
        }
        return 0L;
    }

    @Override
    public AutoBuildTransactionResponse autoBuildTransaction(AutoBuildTransactionRequest request) {
        //校验[非找零]收款方
        List<Payee> nonChangePayees = request.getNonChangePayees();
        if(nonChangePayees == null || nonChangePayees.isEmpty()){
            AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage(PayAlert.PAYEE_CAN_NOT_EMPTY);
            return response;
        }
        for(Payee payee : nonChangePayees){
            if(StringUtil.isEmpty(payee.getAddress())){
                AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
                response.setBuildTransactionSuccess(false);
                response.setMessage(PayAlert.PAYEE_ADDRESS_CAN_NOT_EMPTY);
                return response;
            }
            if(payee.getValue() <= 0){
                AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
                response.setBuildTransactionSuccess(false);
                response.setMessage(PayAlert.PAYEE_VALUE_CAN_NOT_LESS_EQUAL_THAN_ZERO);
                return response;
            }
        }

        //创建付款方
        List<Payer> payers = new ArrayList<>();
        //遍历钱包里的账户,用钱包里的账户付款
        List<Account> allAccounts = getNonZeroBalanceAccounts();
        if(allAccounts != null){
            for(Account account:allAccounts){
                TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(account.getAddress());
                //构建一个新的付款方
                Payer payer = new Payer();
                payer.setPrivateKey(account.getPrivateKey());
                payer.setAddress(account.getAddress());
                payer.setTransactionHash(utxo.getTransactionHash());
                payer.setTransactionOutputIndex(utxo.getTransactionOutputIndex());
                payer.setValue(utxo.getValue());
                payers.add(payer);
                //设置默认手续费
                long fee = 0L;
                boolean haveEnoughMoneyToPay = haveEnoughMoneyToPay(payers,nonChangePayees,fee);
                if(haveEnoughMoneyToPay){
                    //创建一个找零账户，并将找零账户保存在钱包里。
                    Account changeAccount = createAndSaveAccount();
                    //创建一个找零收款方
                    Payee changePayee = createChangePayee(payers,nonChangePayees,changeAccount.getAddress(),fee);
                    //创建收款方(收款方=[非找零]收款方+[找零]收款方)
                    List<Payee> payees = new ArrayList<>();
                    payees.addAll(nonChangePayees);
                    if(changePayee != null){
                        payees.add(changePayee);
                    }
                    //构造交易
                    TransactionDto transactionDto = buildTransaction(payers,payees);
                    AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
                    response.setBuildTransactionSuccess(true);
                    response.setTransaction(transactionDto);
                    response.setTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
                    response.setFee(fee);
                    response.setPayers(payers);
                    response.setNonChangePayees(nonChangePayees);
                    response.setChangePayee(changePayee);
                    response.setPayees(payees);
                    return response;
                }
            }
        }
        AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
        response.setMessage(PayAlert.NOT_ENOUGH_MONEY_TO_PAY);
        response.setBuildTransactionSuccess(false);
        return response;
    }




    private String getWalletDatabasePath() {
        return FileUtil.newPath(coreConfiguration.getCorePath(), WALLET_DATABASE_NAME);
    }
    private byte[] getKeyByAddress(String address){
        return ByteUtil.stringToUtf8Bytes(address);
    }
    private byte[] getKeyByAccount(Account account){
        return getKeyByAddress(account.getAddress());
    }


    private boolean haveEnoughMoneyToPay(List<Payer> payers, List<Payee> payees, long fee) {
        //计算找零金额
        long changeValue = changeValue(payers,payees,fee);
        //判断是否有足够的金额去支付
        boolean haveEnoughMoneyToPay = changeValue>=0;
        return haveEnoughMoneyToPay;
    }
    private Payee createChangePayee(List<Payer> payers, List<Payee> payees, String changeAddress, long fee) {
        //计算找零金额
        long changeValue = changeValue(payers,payees,fee);
        if(changeValue >0){
            //构造找零收款方
            Payee changePayee = new Payee();
            changePayee.setAddress(changeAddress);
            changePayee.setValue(changeValue);
            return changePayee;
        }
        return null;
    }
    private long changeValue(List<Payer> payers, List<Payee> payees, long fee) {
        //交易输入总金额
        long transactionInputValues = 0;
        for(Payer payer: payers){
            transactionInputValues += payer.getValue();
        }
        //收款方收款总金额
        long payeeValues = 0;
        if(payees != null){
            for(Payee payee : payees){
                payeeValues += payee.getValue();
            }
        }
        //计算找零金额，找零金额=交易输入金额-收款方交易输出金额-交易手续费
        long changeValue = transactionInputValues -  payeeValues - fee;
        return changeValue;
    }
    private TransactionDto buildTransaction(List<Payer> payers, List<Payee> payees) {
        //构建交易输入
        List<TransactionInputDto> transactionInputs = new ArrayList<>();
        for(Payer payer: payers){
            TransactionInputDto transactionInput = new TransactionInputDto();
            transactionInput.setTransactionHash(payer.getTransactionHash());
            transactionInput.setTransactionOutputIndex(payer.getTransactionOutputIndex());
            transactionInputs.add(transactionInput);
        }
        //构建交易输出
        List<TransactionOutputDto> transactionOutputs = new ArrayList<>();
        //构造收款方交易输出
        if(payees != null){
            for(Payee payee : payees){
                TransactionOutputDto transactionOutput = new TransactionOutputDto();
                OutputScriptDto outputScript = ScriptDtoTool.createPayToPublicKeyHashOutputScript(payee.getAddress());
                transactionOutput.setValue(payee.getValue());
                transactionOutput.setOutputScript(outputScript);
                transactionOutputs.add(transactionOutput);
            }
        }
        //构造交易
        TransactionDto transaction = new TransactionDto();
        transaction.setInputs(transactionInputs);
        transaction.setOutputs(transactionOutputs);
        //签名
        for(int i=0; i<transactionInputs.size(); i++){
            TransactionInputDto transactionInput = transactionInputs.get(i);
            Account account = AccountUtil.accountFromPrivateKey(payers.get(i).getPrivateKey());
            String signature = TransactionDtoTool.signature(account.getPrivateKey(),transaction);
            InputScriptDto inputScript = ScriptDtoTool.createPayToPublicKeyHashInputScript(signature, account.getPublicKey());
            transactionInput.setInputScript(inputScript);
        }
        return transaction;
    }
}
