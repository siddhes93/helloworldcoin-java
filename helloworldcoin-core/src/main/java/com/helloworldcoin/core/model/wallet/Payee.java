package com.helloworldcoin.core.model.wallet;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class Payee {

    //Payee's address
    private String address;
    //The amount received by the payee
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
