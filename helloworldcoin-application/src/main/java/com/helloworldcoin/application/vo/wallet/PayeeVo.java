package com.helloworldcoin.application.vo.wallet;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class PayeeVo {

    private String address;

    private long value;




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
    //endregion
}
