package com.helloworldcoin.core.model.transaction;


import com.helloworldcoin.core.model.script.OutputScript;

import java.io.Serializable;

/**
 * Transaction output: The recipient of the transaction is called the transaction output.
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionOutput implements Serializable {

    /**
     * Amount of transaction output
     */
    private long value;
    /**
     * Output Script
     */
    private OutputScript outputScript;

    /**
     * Transaction Hash
     * redundancy
     */
    private String transactionHash;
    /**
     * The sequence number of the transaction output in [transaction output of this transaction],
     * the sequence number starts from 1.
     * redundancy
     */
    private long transactionOutputIndex;
    /**
     * The address of the transaction output
     * redundancy: address can be parsed from [output script]
     */
    private String address;

    /**
     * The block height of the block where the transaction that produced the transaction output is located.
     * redundancy
     */
    private long blockHeight;
    /**
     * The block hash of the block where the transaction that produced the transaction output is located.
     * redundancy
     */
    private String blockHash;
    /**
     * The height in the blockchain of the transaction that produced the transaction output.
     * redundancy
     */
    private long transactionHeight;
    /**
     * The transaction sequence number in the block of the transaction that produced the transaction output.
     * redundancy
     */
    private long transactionIndex;
    /**
     * The height of the transaction output in the blockchain, which is a global height.
     * The first transaction output height in the blockchain system is 1,
     * and the subsequent transaction output heights increase by 1.
     * redundancy
     */
    private long transactionOutputHeight;




    //region get set
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public OutputScript getOutputScript() {
        return outputScript;
    }

    public void setOutputScript(OutputScript outputScript) {
        this.outputScript = outputScript;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public long getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(long transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public long getTransactionOutputHeight() {
        return transactionOutputHeight;
    }

    public void setTransactionOutputHeight(long transactionOutputHeight) {
        this.transactionOutputHeight = transactionOutputHeight;
    }

    public long getTransactionHeight() {
        return transactionHeight;
    }

    public void setTransactionHeight(long transactionHeight) {
        this.transactionHeight = transactionHeight;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
    //endregion
}
