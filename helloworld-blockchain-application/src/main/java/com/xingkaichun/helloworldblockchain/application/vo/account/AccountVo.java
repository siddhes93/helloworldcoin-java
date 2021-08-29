package com.xingkaichun.helloworldblockchain.application.vo.account;


import java.io.Serializable;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class AccountVo implements Serializable {

    //私钥
    private String privateKey;
    //公钥
    private String publicKey;
    //公钥哈希
    private String publicKeyHash;
    //地址
    private String address;

    public AccountVo(String privateKey, String publicKey, String publicKeyHash, String address) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.publicKeyHash = publicKeyHash;
        this.address = address;
    }


    //region get set
    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAddress() {
        return address;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }
    //endregion
}
