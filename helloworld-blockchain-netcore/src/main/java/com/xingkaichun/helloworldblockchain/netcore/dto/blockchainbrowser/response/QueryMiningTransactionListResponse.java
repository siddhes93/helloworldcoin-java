package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryMiningTransactionListResponse {

    private List<TransactionDTO> transactionDtoList;




    //region get set

    public List<TransactionDTO> getTransactionDtoList() {
        return transactionDtoList;
    }

    public void setTransactionDtoList(List<TransactionDTO> transactionDtoList) {
        this.transactionDtoList = transactionDtoList;
    }

    //endregion
}
