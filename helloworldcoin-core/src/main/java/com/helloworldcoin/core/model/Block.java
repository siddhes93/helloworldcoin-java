package com.helloworldcoin.core.model;


import com.helloworldcoin.core.model.transaction.Transaction;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class Block implements Serializable {

    /**
     * A block timestamp is a time of block generation.
     */
    private long timestamp;
    /**
     * Block height: the height of the genesis block is 0, and the height of the standard block starts from 1 and increases by 1 in turn.
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private long height;
    /**
     * Previous Hash: hash of the previous block.
     */
    private String previousHash;
    /**
     * transactions in blocks
     */
    private List<Transaction> transactions;
    /**
     * Merkle Tree Root
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private String merkleTreeRoot;
    /**
     * Nonce is the central part of Proof of Work.
     * The Nonce is a random whole number, which is a 64-bit (8 byte) field, which is adjusted by the miners, so that it becomes a valid number to be used for hashing the value of block.
     * Nonce is the number which can be used only once. Once the perfect Nonce is found, it is added to the block.
     * Along with this number, the hash value of that block will get rehashed.
     */
    private String nonce;
    /**
     * A Block Hash or just Hash is a unique string of characters (numbers and letters) that identifies a specific block.
     * Think of it as a fingerprint - if you have the hash, then a search of that hash on the block explorer  will give you the corresponding block it relates to, with all the information in it.
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private String hash;

    /**
     * 挖矿难度
     * 这里保存的是一个十六进制数据。
     * 如果区块哈希十六进制表示小于这个值，则认为挖矿成功
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private String difficulty;

    /**
     * 区块中的交易总笔数
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private long transactionCount;

    /**
     * 上一个区块最后一笔交易在所有交易中的高度。
     * 这个序列号是站在整个区块链的角度而产生的，而不是站在这个区块的角度而产生的。
     * 它的值等于：(高度低于当前区块的所有区块中包含的)交易数量之和
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private long previousTransactionHeight;




    //region get set
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getMerkleTreeRoot() {
        return merkleTreeRoot;
    }

    public void setMerkleTreeRoot(String merkleTreeRoot) {
        this.merkleTreeRoot = merkleTreeRoot;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public long getPreviousTransactionHeight() {
        return previousTransactionHeight;
    }

    public void setPreviousTransactionHeight(long previousTransactionHeight) {
        this.previousTransactionHeight = previousTransactionHeight;
    }
    //endregion
}
