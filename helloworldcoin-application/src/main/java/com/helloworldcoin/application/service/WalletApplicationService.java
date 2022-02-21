package com.helloworldcoin.application.service;

import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.helloworldcoin.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionRequest;
import com.helloworldcoin.application.vo.wallet.AutomaticBuildTransactionResponse;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public interface WalletApplicationService {


    SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request);

    AutomaticBuildTransactionResponse automaticBuildTransaction(AutomaticBuildTransactionRequest request);
}
