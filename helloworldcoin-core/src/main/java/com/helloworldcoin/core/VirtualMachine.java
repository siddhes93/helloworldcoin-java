package com.helloworldcoin.core;

import com.helloworldcoin.core.model.script.Script;
import com.helloworldcoin.core.model.script.Result;
import com.helloworldcoin.core.model.transaction.Transaction;

/**
 * 虚拟机
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class VirtualMachine {

    /**
     * 执行脚本
     */
    public abstract Result execute(Transaction transactionEnvironment, Script script) throws RuntimeException ;

}
