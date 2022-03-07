package com.helloworldcoin.core.model.transaction;

import com.helloworldcoin.core.model.script.InputScript;

import java.io.Serializable;

/**
 * Transaction Input: The payer of a transaction is called a transaction input.
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionInput implements Serializable {

    /**
     * The input of a transaction comes from the output of another transaction
     * The input of the transaction is an unspent transaction output before this transaction occurs.
     */
    private TransactionOutput unspentTransactionOutput;
    /**
     * Input Script
     */
    private InputScript inputScript;




    //region get set
    public TransactionOutput getUnspentTransactionOutput() {
        return unspentTransactionOutput;
    }

    public void setUnspentTransactionOutput(TransactionOutput unspentTransactionOutput) {
        this.unspentTransactionOutput = unspentTransactionOutput;
    }

    public InputScript getInputScript() {
        return inputScript;
    }

    public void setInputScript(InputScript inputScript) {
        this.inputScript = inputScript;
    }
    //endregion
}