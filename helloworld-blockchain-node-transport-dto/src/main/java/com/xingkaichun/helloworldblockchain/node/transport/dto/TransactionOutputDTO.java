package com.xingkaichun.helloworldblockchain.node.transport.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易输出
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class TransactionOutputDTO implements Serializable {

    //交易输出的地址
    private String address;
    //交易输出的金额
    private String value;
    //脚本锁
    private List<String> scriptLock;
}
