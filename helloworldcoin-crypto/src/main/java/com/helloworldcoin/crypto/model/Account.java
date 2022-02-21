package com.helloworldcoin.crypto.model;


import java.io.Serializable;

/**
 * digital currency account
 *
 * digital currency account (account is called wallet in the blockchain field) consists of account number (account is called address in the blockchain field) and password (the password is called private key in the blockchain field).
 *
 * private key can deduce public key, but public key cannot deduce private key.
 * public key can deduce public key hash, but public key hash cannot deduce public key.
 * public key hash can deduce address, and address can deduce public key hash.
 *
 * @author x.king xdotking@gmail.com
 */
public class Account implements Serializable {

    //private key
    private String privateKey;
    //public Key
    private String publicKey;
    //public key hash
    private String publicKeyHash;
    //address
    private String address;

    public Account(String privateKey, String publicKey, String publicKeyHash, String address) {
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
