package com.helloworldcoin.application.vo.account;


/**
 *
 * @author x.king xdotking@gmail.com
 */
public class AccountVo {

    private String privateKey;
    private String publicKey;
    private String publicKeyHash;
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
