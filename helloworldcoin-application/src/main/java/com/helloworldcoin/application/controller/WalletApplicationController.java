package com.helloworldcoin.application.controller;

import com.helloworldcoin.application.service.WalletApplicationService;
import com.helloworldcoin.application.vo.WalletApplicationApi;
import com.helloworldcoin.application.vo.account.*;
import com.helloworldcoin.application.vo.framwork.Response;
import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionRequest;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionResponse;
import com.helloworldcoin.core.Wallet;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.crypto.model.Account;
import com.helloworldcoin.netcore.BlockchainNetCore;
import com.helloworldcoin.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Wallet Application Controller : create account、save account、delete account and so on.
 *
 * @author x.king xdotking@gmail.com
 */
@RestController
public class WalletApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private WalletApplicationService walletApplicationService;



    /**
     * create account
     */
    @RequestMapping(value = WalletApplicationApi.CREATE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request){
        try {
            Account account = AccountUtil.randomAccount();
            AccountVo accountVo = new AccountVo(account.getPrivateKey(),account.getPublicKey(),account.getPublicKeyHash(),account.getAddress());
            CreateAccountResponse response = new CreateAccountResponse();
            response.setAccount(accountVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "'create account' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * create and save account
     */
    @RequestMapping(value = WalletApplicationApi.CREATE_AND_SAVE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<CreateAndSaveAccountResponse> createAndSaveAccount(@RequestBody CreateAndSaveAccountRequest request){
        try {
            Account account = blockchainNetCore.getBlockchainCore().getWallet().createAndSaveAccount();
            AccountVo accountVo = new AccountVo(account.getPrivateKey(),account.getPublicKey(),account.getPublicKeyHash(),account.getAddress());
            CreateAndSaveAccountResponse response = new CreateAndSaveAccountResponse();
            response.setAccount(accountVo);
            return Response.success(response);
        } catch (Exception e){
            String message = "'create and save account' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * save account
     */
    @RequestMapping(value = WalletApplicationApi.SAVE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<SaveAccountResponse> saveAccount(@RequestBody SaveAccountRequest request){
        try {
            String privateKey = request.getPrivateKey();
            Account account = AccountUtil.accountFromPrivateKey(privateKey);
            blockchainNetCore.getBlockchainCore().getWallet().saveAccount(account);
            SaveAccountResponse response = new SaveAccountResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'save account' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * delete account
     */
    @RequestMapping(value = WalletApplicationApi.DELETE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeleteAccountResponse> deleteAccount(@RequestBody DeleteAccountRequest request){
        try {
            String address = request.getAddress();
            blockchainNetCore.getBlockchainCore().getWallet().deleteAccountByAddress(address);
            DeleteAccountResponse response = new DeleteAccountResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'delete account' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * query all accounts
     */
    @RequestMapping(value = WalletApplicationApi.QUERY_ALL_ACCOUNTS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryAllAccountsResponse> queryAllAccounts(@RequestBody QueryAllAccountsRequest request){
        try {
            Wallet wallet = blockchainNetCore.getBlockchainCore().getWallet();
            List<Account> allAccounts = wallet.getAllAccounts();

            List<AccountVo2> accountVos = new ArrayList<>();
            if(allAccounts != null){
                for(Account account:allAccounts){
                    AccountVo2 accountVo = new AccountVo2();
                    accountVo.setAddress(account.getAddress());
                    accountVo.setPrivateKey(account.getPrivateKey());
                    accountVo.setValue(wallet.getBalanceByAddress(account.getAddress()));
                    accountVos.add(accountVo);
                }
            }

            long balance = 0;
            for(AccountVo2 accountVo : accountVos){
                balance += accountVo.getValue();
            }

            QueryAllAccountsResponse response = new QueryAllAccountsResponse();
            response.setAccounts(accountVos);
            response.setBalance(balance);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query all accounts' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * automatic build transaction
     */
    @RequestMapping(value = WalletApplicationApi.AUTOMATIC_BUILD_TRANSACTION,method={RequestMethod.GET,RequestMethod.POST})
    public Response<AutomaticBuildTransactionResponse> automaticBuildTransaction(@RequestBody AutomaticBuildTransactionRequest request){
        try {
            AutomaticBuildTransactionResponse autoBuildTransactionResponse = walletApplicationService.automaticBuildTransaction(request);
            if(autoBuildTransactionResponse.isBuildTransactionSuccess()){
                return Response.success(autoBuildTransactionResponse);
            }else {
                return Response.serviceUnavailable();
            }
        } catch (Exception e){
            String message = "'automatic build transaction' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * submit transaction to blockchain network
     */
    @RequestMapping(value = WalletApplicationApi.SUBMIT_TRANSACTION_TO_BLOCKCHIAIN_NEWWORK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<SubmitTransactionToBlockchainNetworkResponse> submitTransactionToBlockchainNetwork(@RequestBody SubmitTransactionToBlockchainNetworkRequest request){
        try {
            SubmitTransactionToBlockchainNetworkResponse response = walletApplicationService.submitTransactionToBlockchainNetwork(request);
            return Response.success(response);
        } catch (Exception e){
            String message = "'submit transaction to blockchain network' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }


}