package com.xingkaichun.helloworldblockchain.application.vo.account;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class AccountVo2 {
    private String privateKey;
    private String address;
    private long value;

    public AccountVo2(String privateKey, String address, long value) {
        this.privateKey = privateKey;
        this.address = address;
        this.value = value;
    }

    public AccountVo2() {
    }

    //region get set
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
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
