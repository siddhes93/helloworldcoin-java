package com.helloworldcoin.core.model.wallet;

/**
 * 收款方
 *
 * @author x.king xdotking@gmail.com
 */
public class Payee {

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
