package com.xingkaichun.helloworldblockchain.explorer.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionOutputListByAddressResponse {

    private TransactionOutputDetailView transactionOutputDetailView;

    public TransactionOutputDetailView getTransactionOutputDetailView() {
        return transactionOutputDetailView;
    }

    public void setTransactionOutputDetailView(TransactionOutputDetailView transactionOutputDetailView) {
        this.transactionOutputDetailView = transactionOutputDetailView;
    }
}
