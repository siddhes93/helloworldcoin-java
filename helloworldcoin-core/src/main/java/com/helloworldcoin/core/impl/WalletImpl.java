package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.BlockchainDatabase;
import com.helloworldcoin.core.CoreConfiguration;
import com.helloworldcoin.core.Wallet;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionRequest;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionResponse;
import com.helloworldcoin.core.model.wallet.Payee;
import com.helloworldcoin.core.model.wallet.Payer;
import com.helloworldcoin.core.tool.ScriptDtoTool;
import com.helloworldcoin.core.tool.TransactionDtoTool;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.crypto.model.Account;
import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.EncodeDecodeTool;
import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.KvDbUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
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
        List<Payee> nonChangePayees = request.getNonChangePayees();
        List<Payer> payers = new ArrayList<>();
        List<Account> allAccounts = getNonZeroBalanceAccounts();
        if(allAccounts != null){
            for(Account account:allAccounts){
                TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(account.getAddress());
                Payer payer = new Payer();
                payer.setPrivateKey(account.getPrivateKey());
                payer.setAddress(account.getAddress());
                payer.setTransactionHash(utxo.getTransactionHash());
                payer.setTransactionOutputIndex(utxo.getTransactionOutputIndex());
                payer.setValue(utxo.getValue());
                payers.add(payer);
                long fee = 0L;
                boolean haveEnoughMoneyToPay = haveEnoughMoneyToPay(payers,nonChangePayees,fee);
                if(haveEnoughMoneyToPay){
                    Account changeAccount = createAndSaveAccount();
                    Payee changePayee = createChangePayee(payers,nonChangePayees,changeAccount.getAddress(),fee);
                    List<Payee> payees = new ArrayList<>();
                    payees.addAll(nonChangePayees);
                    if(changePayee != null){
                        payees.add(changePayee);
                    }
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
        long changeValue = changeValue(payers,payees,fee);
        boolean haveEnoughMoneyToPay = changeValue>=0;
        return haveEnoughMoneyToPay;
    }
    private Payee createChangePayee(List<Payer> payers, List<Payee> payees, String changeAddress, long fee) {
        long changeValue = changeValue(payers,payees,fee);
        if(changeValue >0){
            Payee changePayee = new Payee();
            changePayee.setAddress(changeAddress);
            changePayee.setValue(changeValue);
            return changePayee;
        }
        return null;
    }
    private long changeValue(List<Payer> payers, List<Payee> payees, long fee) {
        long transactionInputValues = 0;
        for(Payer payer: payers){
            transactionInputValues += payer.getValue();
        }
        long payeeValues = 0;
        if(payees != null){
            for(Payee payee : payees){
                payeeValues += payee.getValue();
            }
        }
        long changeValue = transactionInputValues -  payeeValues - fee;
        return changeValue;
    }
    private TransactionDto buildTransaction(List<Payer> payers, List<Payee> payees) {
        List<TransactionInputDto> transactionInputs = new ArrayList<>();
        for(Payer payer: payers){
            TransactionInputDto transactionInput = new TransactionInputDto();
            transactionInput.setTransactionHash(payer.getTransactionHash());
            transactionInput.setTransactionOutputIndex(payer.getTransactionOutputIndex());
            transactionInputs.add(transactionInput);
        }
        List<TransactionOutputDto> transactionOutputs = new ArrayList<>();
        if(payees != null){
            for(Payee payee : payees){
                TransactionOutputDto transactionOutput = new TransactionOutputDto();
                OutputScriptDto outputScript = ScriptDtoTool.createPayToPublicKeyHashOutputScript(payee.getAddress());
                transactionOutput.setValue(payee.getValue());
                transactionOutput.setOutputScript(outputScript);
                transactionOutputs.add(transactionOutput);
            }
        }
        TransactionDto transaction = new TransactionDto();
        transaction.setInputs(transactionInputs);
        transaction.setOutputs(transactionOutputs);
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
