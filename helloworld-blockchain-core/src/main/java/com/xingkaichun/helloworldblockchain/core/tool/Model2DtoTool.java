package com.xingkaichun.helloworldblockchain.core.tool;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * model转dto工具
 *
 * @author 邢开春 409060350@qq.com
 */
public class Model2DtoTool {

    public static BlockDto block2BlockDto(Block block) {
        List<TransactionDto> transactionDtos = new ArrayList<>();
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                TransactionDto transactionDto = transaction2TransactionDto(transaction);
                transactionDtos.add(transactionDto);
            }
        }

        BlockDto blockDto = new BlockDto();
        blockDto.setTimestamp(block.getTimestamp());
        blockDto.setPreviousHash(block.getPreviousHash());
        blockDto.setTransactions(transactionDtos);
        blockDto.setNonce(block.getNonce());
        return blockDto;
    }

    public static TransactionDto transaction2TransactionDto(Transaction transaction) {
        List<TransactionInputDto> inputs = new ArrayList<>();
        List<TransactionInput> transactionInputs = transaction.getInputs();
        if(transactionInputs!=null){
            for (TransactionInput transactionInput:transactionInputs){
                TransactionInputDto transactionInputDto = new TransactionInputDto();
                transactionInputDto.setTransactionHash(transactionInput.getUnspentTransactionOutput().getTransactionHash());
                transactionInputDto.setTransactionOutputIndex(transactionInput.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputDto.setInputScript(inputScript2InputScriptDto(transactionInput.getInputScript()));
                inputs.add(transactionInputDto);
            }
        }

        List<TransactionOutputDto> outputs = new ArrayList<>();
        List<TransactionOutput> transactionOutputs = transaction.getOutputs();
        if(transactionOutputs!=null){
            for(TransactionOutput transactionOutput:transactionOutputs){
                TransactionOutputDto transactionOutputDto = transactionOutput2TransactionOutputDto(transactionOutput);
                outputs.add(transactionOutputDto);
            }
        }

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setInputs(inputs);
        transactionDto.setOutputs(outputs);
        return transactionDto;
    }

    public static InputScriptDto inputScript2InputScriptDto(InputScript inputScript) {
        InputScriptDto inputScriptDto = new InputScriptDto();
        if(inputScript != null){
            inputScriptDto.addAll(inputScript);
        }
        return inputScriptDto;
    }

    public static OutputScriptDto outputScript2OutputScriptDto(OutputScript outputScript) {
        OutputScriptDto outputScriptDto = new OutputScriptDto();
        if(outputScript != null){
            outputScriptDto.addAll(outputScript);
        }
        return outputScriptDto;
    }

    public static TransactionOutputDto transactionOutput2TransactionOutputDto(TransactionOutput transactionOutput) {
        TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
        transactionOutputDto.setValue(transactionOutput.getValue());
        transactionOutputDto.setOutputScript(outputScript2OutputScriptDto(transactionOutput.getOutputScript()));
        return transactionOutputDto;
    }
}
