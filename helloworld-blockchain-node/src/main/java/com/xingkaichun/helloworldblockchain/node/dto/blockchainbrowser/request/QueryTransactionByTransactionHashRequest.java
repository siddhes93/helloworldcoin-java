package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryTransactionByTransactionHashRequest {

    private String transactionHash;
}
