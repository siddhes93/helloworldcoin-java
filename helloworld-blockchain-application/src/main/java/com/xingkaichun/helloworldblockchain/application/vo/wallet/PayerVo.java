package com.xingkaichun.helloworldblockchain.application.vo.wallet;

/**
 * 付款方
 *
 * @author 邢开春 409060350@qq.com
 */
public class PayerVo {

    /**
     * 付款方私钥
     */
    private String privateKey;

    /**
     * 付款来源的交易哈希
     */
    private String transactionHash;
    /**
     * 付款来源的交易输出序列号。
     */
    private long transactionOutputIndex;
    /**
     * 付款来源的交易输出的金额
     */
    private long value;

    /**
     * 付款方地址
     */
    private String address;


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
}
