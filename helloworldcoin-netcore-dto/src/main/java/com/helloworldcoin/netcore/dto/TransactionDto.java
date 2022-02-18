package com.helloworldcoin.netcore.dto;


import java.io.Serializable;
import java.util.List;

/**
 * @see com.helloworldcoin.core.model.transaction.Transaction
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionDto implements Serializable {

    //inputs of transaction
    private List<TransactionInputDto> inputs;
    //outputs of transaction
    private List<TransactionOutputDto> outputs;




    //region get set
    public List<TransactionInputDto> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInputDto> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutputDto> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutputDto> outputs) {
        this.outputs = outputs;
    }
    //endregion
}