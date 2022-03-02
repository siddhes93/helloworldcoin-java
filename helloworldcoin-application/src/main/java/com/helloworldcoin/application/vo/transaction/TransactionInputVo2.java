package com.helloworldcoin.application.vo.transaction;

/**
 * @author x.king xdotking@gmail.com
 */
public class TransactionInputVo2 {

    private long value;
    private String address;
    private String transactionHash;
    private long transactionOutputIndex;




    //region get set
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
