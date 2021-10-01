package com.xingkaichun.helloworldblockchain.application.controller;

import com.xingkaichun.helloworldblockchain.application.service.WalletApplicationService;
import com.xingkaichun.helloworldblockchain.application.vo.WalletApplicationApi;
import com.xingkaichun.helloworldblockchain.application.vo.account.*;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.Response;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.xingkaichun.helloworldblockchain.application.vo.wallet.AutomaticBuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.application.vo.wallet.AutomaticBuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱包应用控制器：新增账户，删除账户、查询账户、构建交易等。
 * 这里的操作都应该需要权限才可以操作，不适合对所有人开放。
 *
 * @author 邢开春 409060350@qq.com
 */
@RestController
public class WalletApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private WalletApplicationService walletApplicationService;



    /**
     * 生成账户(私钥、公钥、公钥哈希、地址)
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
            String message = "生成账户失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 生成账户(私钥、公钥、公钥哈希、地址)并保存
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
            String message = "[生成账户并保存]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 新增账户
     */
    @RequestMapping(value = WalletApplicationApi.SAVE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<SaveAccountResponse> saveAccount(@RequestBody SaveAccountRequest request){
        try {
            String privateKey = request.getPrivateKey();
            if(StringUtil.isEmpty(privateKey)){
                return Response.requestParamIllegal();
            }
            Account account = AccountUtil.accountFromPrivateKey(privateKey);
            blockchainNetCore.getBlockchainCore().getWallet().saveAccount(account);
            SaveAccountResponse response = new SaveAccountResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "新增账户失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 删除账户
     */
    @RequestMapping(value = WalletApplicationApi.DELETE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeleteAccountResponse> deleteAccount(@RequestBody DeleteAccountRequest request){
        try {
            String address = request.getAddress();
            if(StringUtil.isEmpty(address)){
                return Response.requestParamIllegal();
            }
            blockchainNetCore.getBlockchainCore().getWallet().deleteAccountByAddress(address);
            DeleteAccountResponse response = new DeleteAccountResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "删除账号失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 查询所有的账户
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
            String message = "[查询所有账户]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 构建交易
     */
    @RequestMapping(value = WalletApplicationApi.AUTOMATIC_BUILD_TRANSACTION,method={RequestMethod.GET,RequestMethod.POST})
    public Response<AutomaticBuildTransactionResponse> automaticBuildTransaction(@RequestBody AutomaticBuildTransactionRequest request){
        try {
            //构建交易
            AutomaticBuildTransactionResponse autoBuildTransactionResponse = walletApplicationService.automaticBuildTransaction(request);
            if(autoBuildTransactionResponse.isBuildTransactionSuccess()){
                return Response.success(autoBuildTransactionResponse);
            }else {
                return Response.fail(autoBuildTransactionResponse.getMessage());
            }
        } catch (Exception e){
            String message = "构建交易失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }

    /**
     * 提交交易到区块链网络
     */
    @RequestMapping(value = WalletApplicationApi.SUBMIT_TRANSACTION_TO_BLOCKCHIAIN_NEWWORK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<SubmitTransactionToBlockchainNetworkResponse> submitTransactionToBlockchainNetwork(@RequestBody SubmitTransactionToBlockchainNetworkRequest request){
        try {
            SubmitTransactionToBlockchainNetworkResponse response = walletApplicationService.submitTransactionToBlockchainNetwork(request);
            return Response.success(response);
        } catch (Exception e){
            String message = "提交交易到区块链网络失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }


}