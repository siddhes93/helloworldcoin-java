package com.xingkaichun.helloworldblockchain.application.service;

import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.xingkaichun.helloworldblockchain.application.vo.wallet.AutomaticBuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.application.vo.wallet.AutomaticBuildTransactionResponse;

/**
 * 钱包应用service
 *
 * @author 邢开春 409060350@qq.com
 */
public interface WalletApplicationService {


    /**
     * 提交交易到区块链网络
     */
    SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request);
    /**
     * 自动构建交易
     */
    AutomaticBuildTransactionResponse automaticBuildTransaction(AutomaticBuildTransactionRequest request);
}
