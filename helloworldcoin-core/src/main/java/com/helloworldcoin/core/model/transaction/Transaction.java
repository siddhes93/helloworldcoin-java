package com.helloworldcoin.core.model.transaction;


import java.io.Serializable;
import java.util.List;

/**
 * Transaction: The transfer activity between the payer and the payee is called a transaction.
 *
 * @author x.king xdotking@gmail.com
 */
public class Transaction implements Serializable {

    /**
     * transaction hash
     * The transaction hash is used to represent a unique transaction number.
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private String transactionHash;
    /**
     * Transaction Type
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private TransactionType transactionType;
    /**
     * Transaction Input: The payer of the transaction
     */
    private List<TransactionInput> inputs;
    /**
     * Transaction output: The payee of the transaction
     */
    private List<TransactionOutput> outputs;
    /**
     * The serial number of the transaction in the block.
     * The serial number of the first transaction in each block is calculated from 1,
     * and the serial number of subsequent transactions is incremented by 1.
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private long transactionIndex;
    /**
     * The height of the transaction in the blockchain, which is a global height.
     * The first transaction in the blockchain system has a transaction height of 1,
     * and the height of subsequent transactions increases by 1.
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private long transactionHeight;
    /**
     * The block height of the block where the transaction is located.
     *
     * Redundant field, this value can be calculated by the blockchain system
     */
    private long blockHeight;




    //region get set
    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    public long getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(long transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public long getTransactionHeight() {
        return transactionHeight;
    }

    public void setTransactionHeight(long transactionHeight) {
        this.transactionHeight = transactionHeight;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }
    //endregion
}
