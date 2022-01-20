package com.helloworldcoin.netcore.dto;


import java.io.Serializable;

/**
 * 交易输出
 * 属性含义参考
 * @see com.helloworldcoin.core.model.transaction.TransactionOutput
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionOutputDto implements Serializable {

    //[输出脚本]
    private OutputScriptDto outputScript;

    //交易输出的金额
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
