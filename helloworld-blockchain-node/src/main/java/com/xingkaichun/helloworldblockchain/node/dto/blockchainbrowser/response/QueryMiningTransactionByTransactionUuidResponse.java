package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import lombok.Data;

@Data
public class QueryMiningTransactionByTransactionUuidResponse {

    private TransactionDTO transactionDTO;
}
