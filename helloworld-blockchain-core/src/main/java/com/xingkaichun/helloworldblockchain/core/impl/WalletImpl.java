package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Payee;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Payer;
import com.xingkaichun.helloworldblockchain.core.tools.EncodeDecodeTool;
import com.xingkaichun.helloworldblockchain.core.tools.ScriptDtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionDtoTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class WalletImpl extends Wallet {

    private CoreConfiguration coreConfiguration;
    private static final String WALLET_DATABASE_NAME = "WalletDatabase";

    public WalletImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        //获取所有
        List<byte[]> bytesAccountList = KvDbUtil.gets(getWalletDatabasePath(),1,100000000);
        if(bytesAccountList != null){
            for(byte[] bytesAccount:bytesAccountList){
                Account account = EncodeDecodeTool.decodeToAccount(bytesAccount);
                accountList.add(account);
            }
        }
        return accountList;
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
        KvDbUtil.put(getWalletDatabasePath(),getKeyByAccount(account), EncodeDecodeTool.encodeAccount(account));
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
        //校验收款方
        List<Payee> payees = request.getPayees();
        if(payees == null || payees.isEmpty()){
            AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("收款方不能为空。");
            return response;
        }
        for(Payee payee : payees){
            if(StringUtil.isNullOrEmpty(payee.getAddress())){
                AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
                response.setBuildTransactionSuccess(false);
                response.setMessage("收款方的地址不能为空。");
                return response;
            }
            if(payee.getValue() <= 0){
                AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
                response.setBuildTransactionSuccess(false);
                response.setMessage("收款方收款金额不能小于0。");
                return response;
            }
        }

        //创建付款方
        List<Payer> payers = new ArrayList<>();
        //遍历钱包里的账户,用钱包里的账户付款
        List<Account> allAccounts = getAllAccounts();
        if(allAccounts != null){
            for(Account account:allAccounts){
                TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(account.getAddress());
                //过滤无余额的账户
                if(utxo == null || utxo.getValue() <= 0){
                    continue;
                }
                //构建一个新的付款方
                Payer payer = new Payer();
                payer.setPrivateKey(account.getPrivateKey());
                payer.setAddress(AccountUtil.addressFromPrivateKey(payer.getPrivateKey()));
                payer.setTransactionHash(utxo.getTransactionHash());
                payer.setTransactionOutputIndex(utxo.getTransactionOutputIndex());
                payer.setValue(utxo.getValue());
                payers.add(payer);
                //设置默认手续费
                long fee = 0L;
                boolean haveEnoughMoneyToPay = haveEnoughMoneyToPay(payers,payees,fee);
                if(haveEnoughMoneyToPay){
                    //创建一个找零账户
                    Account changeAccount = createAndSaveAccount();
                    //创建一个找零收款方
                    Payee changePayee = createChangePayee(payers,payees,changeAccount.getAddress(),fee);
                    //创建收款方(包含找零收款方)
                    List<Payee> allPayees = new ArrayList<>();
                    allPayees.addAll(payees);
                    if(changePayee != null){
                        allPayees.add(changePayee);
                    }
                    //构造交易
                    TransactionDto transactionDto = buildTransaction(payers,allPayees);
                    AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
                    response.setBuildTransactionSuccess(true);
                    response.setMessage("构建交易成功");
                    response.setTransaction(transactionDto);
                    response.setTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
                    response.setFee(fee);
                    response.setPayers(payers);
                    response.setExclusionChangePayees(payees);
                    response.setChangePayee(changePayee);
                    return response;
                }
            }
        }
        AutoBuildTransactionResponse response = new AutoBuildTransactionResponse();
        response.setMessage("没有足够的金额去支付！");
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
        //计算付款方最少需要支付的金额，该金额由二部分构成，一是收款方金额 ，二是交易手续费
        long minimumTransactionInputValues = payeeValues + fee;
        //判断是否有足够的金额去支付
        boolean haveEnoughMoneyToPay = transactionInputValues >= minimumTransactionInputValues;
        return haveEnoughMoneyToPay;
    }
    private Payee createChangePayee(List<Payer> payers, List<Payee> payees, String changeAddress, long fee) {
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
        long change = transactionInputValues -  payeeValues - fee;
        //构造找零收款方
        Payee changePayee = new Payee();
        changePayee.setAddress(changeAddress);
        changePayee.setValue(change);
        return changePayee;
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
            Account account = AccountUtil.accountFromPrivateKey(payers.get(i).getPrivateKey());
            TransactionInputDto transactionInput = transactionInputs.get(i);
            String signature = TransactionDtoTool.signature(account.getPrivateKey(),transaction);
            InputScriptDto inputScript = ScriptDtoTool.createPayToPublicKeyHashInputScript(signature, account.getPublicKey());
            transactionInput.setInputScript(inputScript);
        }
        return transaction;
    }
}
