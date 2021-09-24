package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.Result;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

/**
 * 虚拟机
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class VirtualMachine {

    /**
     * 执行脚本
     */
    public abstract Result execute(Transaction transactionEnvironment, Script script) throws RuntimeException ;

}
