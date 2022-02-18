package com.helloworldcoin.netcore.dto;


import java.io.Serializable;

/**
 * @see com.helloworldcoin.core.model.transaction.TransactionOutput
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionOutputDto implements Serializable {

    //output script
    private OutputScriptDto outputScript;

    //value
    private long value;




    //region get set
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public OutputScriptDto getOutputScript() {
        return outputScript;
    }

    public void setOutputScript(OutputScriptDto outputScript) {
        this.outputScript = outputScript;
    }
    //endregion
}
