package com.helloworldcoin.netcore.dto;

import java.io.Serializable;

/**
 * @see com.helloworldcoin.core.model.transaction.TransactionInput
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionInputDto implements Serializable {

    //transaction hash
    private String transactionHash;
    //transaction output index
    private long transactionOutputIndex;
    //input script
    private InputScriptDto inputScript;




    //region get set
    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }

    public InputScriptDto getInputScript() {
        return inputScript;
    }

    public void setInputScript(InputScriptDto inputScript) {
        this.inputScript = inputScript;
    }
    //endregion
}