package com.helloworldcoin.application.vo.transaction;

import com.helloworldcoin.netcore.dto.TransactionDto;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class SubmitTransactionToBlockchainNetworkRequest {

    private TransactionDto transaction;




    //region get set
    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }
    //endregion
}
