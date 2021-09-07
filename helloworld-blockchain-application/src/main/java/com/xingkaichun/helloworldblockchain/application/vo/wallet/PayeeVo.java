package com.xingkaichun.helloworldblockchain.application.vo.wallet;

/**
 * 收款方
 *
 * @author 邢开春 409060350@qq.com
 */
public class PayeeVo {

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
