package com.helloworldcoin.application.vo.transaction;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionOutputVo {

    private String address;
    private long value;
    private String outputScript;
    private String transactionHash;
    private long transactionOutputIndex;




    //region get set
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getOutputScript() {
        return outputScript;
    }

    public void setOutputScript(String outputScript) {
        this.outputScript = outputScript;
    }

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
    //endregion
}
