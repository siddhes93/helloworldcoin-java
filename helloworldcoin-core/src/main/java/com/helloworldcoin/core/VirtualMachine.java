package com.helloworldcoin.core;

import com.helloworldcoin.core.model.script.Script;
import com.helloworldcoin.core.model.script.Result;
import com.helloworldcoin.core.model.transaction.Transaction;

/**
 * Virtual Machine
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class VirtualMachine {

    /**
     * execute script
     */
    public abstract Result execute(Transaction transactionEnvironment, Script script) throws RuntimeException ;

}
