package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 * @author xingkaichun@ceair.com
 */
public class TransactionOutputVo2 {
    private long value;
    private String address;

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
