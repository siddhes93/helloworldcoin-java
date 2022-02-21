package com.helloworldcoin.netcore.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @see com.helloworldcoin.core.model.Block
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockDto implements Serializable {

    //timestamp of block generation
    private long timestamp;
    //previous block hash
    private String previousHash;
    //transactions in block
    private List<TransactionDto> transactions;
    //nonce
    private String nonce;




    //region get set
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    //endregion
}
