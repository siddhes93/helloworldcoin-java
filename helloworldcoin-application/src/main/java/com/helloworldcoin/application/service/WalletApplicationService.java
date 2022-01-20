package com.helloworldcoin.application.service;

import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionRequest;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionResponse;

/**
 * 钱包应用service
 *
 * @author x.king xdotking@gmail.com
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
