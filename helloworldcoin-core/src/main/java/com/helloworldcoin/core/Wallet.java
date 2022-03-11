package com.helloworldcoin.core;

import com.helloworldcoin.core.model.wallet.AutoBuildTransactionRequest;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionResponse;
import com.helloworldcoin.crypto.model.Account;

import java.util.List;

/**
 * Wallet
 * The wallet is used to manage accounts: such as adding accounts, deleting accounts, querying accounts, obtaining accounts, etc.
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class Wallet {

    protected CoreConfiguration coreConfiguration;
    protected BlockchainDatabase blockchainDatabase;


    public abstract List<Account> getAllAccounts();

    public abstract List<Account> getNonZeroBalanceAccounts();

    public abstract Account createAccount();

    public abstract Account createAndSaveAccount();

    public abstract void saveAccount(Account account);

    public abstract void deleteAccountByAddress(String address);

    public abstract long getBalanceByAddress(String address);

    public abstract AutoBuildTransactionResponse autoBuildTransaction(AutoBuildTransactionRequest request) ;
}
