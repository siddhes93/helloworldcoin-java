package com.helloworldcoin.core.model.wallet;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class Payer {

    /**
     * Payer's private key
     */
    private String privateKey;

    /**
     * The transaction hash of the payment source
     */
    private String transactionHash;
    /**
     * The transaction output index of the payment source.
     */
    private long transactionOutputIndex;
    /**
     * Payment amount
     */
    private long value;

    /**
     * Payer's address
     */
    private String address;




    //region get set
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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
    //endregion
}
