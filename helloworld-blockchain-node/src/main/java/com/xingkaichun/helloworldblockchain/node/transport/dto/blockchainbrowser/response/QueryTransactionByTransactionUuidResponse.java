package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import lombok.Data;

@Data
public class QueryTransactionByTransactionUuidResponse {

    private TransactionDTO transactionDTO;
}
