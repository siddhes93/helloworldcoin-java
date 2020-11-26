package com.xingkaichun.helloworldblockchain.core.model.pay;

/**
 * 付款接收方
 */
public class Recipient {

    //交易输出的地址
    private String address;

    //交易输出的金额
    private long value;




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
}
