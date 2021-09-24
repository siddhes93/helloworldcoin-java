package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;

import java.util.List;

/**
 * 钱包
 * 管理拥有的账户（增加账户、删除账户、查询账户、获取账户等）
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class Wallet {

    //配置
    protected CoreConfiguration coreConfiguration;
    //钱包所在的区块链
    protected BlockchainDatabase blockchainDatabase;


    public abstract List<Account> getAllAccounts();

    //非零余额的账户
    public abstract List<Account> getNonZeroBalanceAccounts();

    public abstract Account createAccount();

    public abstract Account createAndSaveAccount();

    public abstract void saveAccount(Account account);

    public abstract void deleteAccountByAddress(String address);

    /**
     * 获取地址余额
     */
    public abstract long getBalanceByAddress(String address);

    /**
     * 构建交易
     * 为了简化用户付款流程，只需要用户提供[非找零]收款方即可，然后，系统会智能的构建出交易。
     *
     * 系统会自动从钱包中拿取(一个或多个)账户当做付款方；
     * 系统会自动的创建一个账户，并将该账户保存至钱包，并将该账户用作接收找零。
     *
     * 我将收款方分为[非找零]收款方和[找零]收款方；
     * 如果User1需要向User2支付10000，User1使用了Account1(账户里有5000)和Account2(账户里有6000)，付款给User2的Account3，
     * Account1(账户里有5000)和Account2(账户里有6000)共有11000，比需要支付的10000多1000，这1000就是找零，应该再返还给User1。
     * 这里User1创建了一个Account4用于接收找零，这个Account4就是[找零]账户。
     * 如果只关心账户钱的流转，会发现，整体付款行为是Account1+Account2->Account3+Account4，
     * 箭头左面的Account1、Account2都是付款方，箭头右面的Account3、Account4都是收款方。
     * Account3、Account4都称呼为收款方，并不能说明Account4是用于接收找零的作用，所以，
     * 我将Account4称为[找零]收款方，相对应的称呼Account3为[非找零]收款方。
     */
    public abstract AutoBuildTransactionResponse autoBuildTransaction(AutoBuildTransactionRequest request) ;
}
