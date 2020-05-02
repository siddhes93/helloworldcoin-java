package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request;

import lombok.Data;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class AddOrUpdateNodeRequest {

    private int port;
    private BigInteger blockChainHeight;
}
