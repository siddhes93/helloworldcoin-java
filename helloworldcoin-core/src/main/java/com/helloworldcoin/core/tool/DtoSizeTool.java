package com.helloworldcoin.core.tool;

import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.setting.BlockSetting;
import com.helloworldcoin.setting.ScriptSetting;
import com.helloworldcoin.setting.TransactionSetting;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.StringUtil;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class DtoSizeTool {

    //region check Size
    /**
     * check Block Size: used to limit the size of the block.
     */
    public static boolean checkBlockSize(BlockDto blockDto) {
        //The length of the timestamp of the block does not need to be verified. If the timestamp length is incorrect, it will not work in the business logic.

        //The length of the previous hash of the block does not need to be verified. If the previous hash length is incorrect, it will not work in the business logic.

        //Check block nonce size
        long nonceSize = sizeOfString(blockDto.getNonce());
        if(nonceSize != BlockSetting.NONCE_CHARACTER_COUNT){
            LogUtil.debug("Illegal nonce length.");
            return false;
        }

        //Check the size of each transaction
        List<TransactionDto> transactionDtos = blockDto.getTransactions();
        if(transactionDtos != null){
            for(TransactionDto transactionDto:transactionDtos){
                if(!checkTransactionSize(transactionDto)){
                    LogUtil.debug("Illegal transaction size.");
                    return false;
                }
            }
        }

        //Check Block size
        long blockSize = calculateBlockSize(blockDto);
        if(blockSize > BlockSetting.BLOCK_MAX_CHARACTER_COUNT){
            LogUtil.debug("Block size exceeds limit.");
            return false;
        }
        return true;
    }
    /**
     * Check transaction size: used to limit the size of the transaction.
     */
    public static boolean checkTransactionSize(TransactionDto transactionDto) {
        //Check transaction input size
        List<TransactionInputDto> transactionInputDtos = transactionDto.getInputs();
        if(transactionInputDtos != null){
            for(TransactionInputDto transactionInputDto:transactionInputDtos){
                //The unspent output size of the transaction does not need to be verified. If the assumption is incorrect, it will not work in the business logic.

                //Check script size
                InputScriptDto inputScriptDto = transactionInputDto.getInputScript();
                //Check the size of the input script
                if(!checkInputScriptSize(inputScriptDto)){
                    return false;
                }
            }
        }

        //Check transaction output size
        List<TransactionOutputDto> transactionOutputDtos = transactionDto.getOutputs();
        if(transactionOutputDtos != null){
            for(TransactionOutputDto transactionOutputDto:transactionOutputDtos){
                //The size of the transaction output amount does not need to be verified. If the assumption is incorrect, it will not work in the business logic.

                //Check script size
                OutputScriptDto outputScriptDto = transactionOutputDto.getOutputScript();
                //Check the size of the output script
                if(!checkOutputScriptSize(outputScriptDto)){
                    return false;
                }

            }
        }

        //Check transaction size
        long transactionSize = calculateTransactionSize(transactionDto);
        if(transactionSize > TransactionSetting.TRANSACTION_MAX_CHARACTER_COUNT){
            LogUtil.debug("Transaction size exceeds limit.");
            return false;
        }
        return true;
    }

    /**
     * check Input Script Size
     */
    public static boolean checkInputScriptSize(InputScriptDto inputScriptDto) {
        if(!checkScriptSize(inputScriptDto)){
            return false;
        }
        return true;
    }

    /**
     * check Output Script Size
     */
    public static boolean checkOutputScriptSize(OutputScriptDto outputScriptDto) {
        if(!checkScriptSize(outputScriptDto)){
            return false;
        }
        return true;
    }

    /**
     * check Script Size
     */
    public static boolean checkScriptSize(ScriptDto scriptDto) {
        //There is no need to check the size of opcodes and operands in the script.
        //Illegal opcodes, illegal operands cannot constitute a legal script.
        //Illegal script will not work in the business logic.
        if(calculateScriptSize(scriptDto) > ScriptSetting.SCRIPT_MAX_CHARACTER_COUNT){
            LogUtil.debug("Script size exceeds limit.");
            return false;
        }
        return true;
    }
    //endregion



    //region calculate Size
    public static long calculateBlockSize(BlockDto blockDto) {
        long size = 0;
        long timestamp = blockDto.getTimestamp();
        size += sizeOfNumber(timestamp);

        String previousBlockHash = blockDto.getPreviousHash();
        size += sizeOfString(previousBlockHash);

        String nonce = blockDto.getNonce();
        size += sizeOfString(nonce);
        List<TransactionDto> transactionDtos = blockDto.getTransactions();
        for(TransactionDto transactionDto:transactionDtos){
            size += calculateTransactionSize(transactionDto);
        }
        return size;
    }
    public static long calculateTransactionSize(TransactionDto transactionDto) {
        long size = 0;
        List<TransactionInputDto> transactionInputDtos = transactionDto.getInputs();
        size += calculateTransactionInputsSize(transactionInputDtos);
        List<TransactionOutputDto> transactionOutputDtos = transactionDto.getOutputs();
        size += calculateTransactionsOutputSize(transactionOutputDtos);
        return size;
    }
    private static long calculateTransactionsOutputSize(List<TransactionOutputDto> transactionOutputDtos) {
        long size = 0;
        if(transactionOutputDtos == null){
            return size;
        }
        for(TransactionOutputDto transactionOutputDto:transactionOutputDtos){
            size += calculateTransactionOutputSize(transactionOutputDto);
        }
        return size;
    }
    private static long calculateTransactionOutputSize(TransactionOutputDto transactionOutputDto) {
        long size = 0;
        OutputScriptDto outputScriptDto = transactionOutputDto.getOutputScript();
        size += calculateScriptSize(outputScriptDto);
        long value = transactionOutputDto.getValue();
        size += sizeOfNumber(value);
        return size;
    }
    private static long calculateTransactionInputsSize(List<TransactionInputDto> inputs) {
        long size = 0;
        if(inputs == null){
            return size;
        }
        for(TransactionInputDto transactionInputDto:inputs){
            size += calculateTransactionInputSize(transactionInputDto);
        }
        return size;
    }
    private static long calculateTransactionInputSize(TransactionInputDto input) {
        long size = 0;
        String transactionHash = input.getTransactionHash();
        size += sizeOfString(transactionHash);
        long transactionOutputIndex = input.getTransactionOutputIndex();
        size += sizeOfNumber(transactionOutputIndex);
        InputScriptDto inputScriptDto = input.getInputScript();
        size += calculateScriptSize(inputScriptDto);
        return size;
    }
    private static long calculateScriptSize(ScriptDto script) {
        long size = 0;
        if(script == null){
            return size;
        }
        for(String scriptCode:script){
            size += sizeOfString(scriptCode);
        }
        return size;
    }


    private static long sizeOfString(String value) {
        return StringUtil.length(value);
    }

    private static long sizeOfNumber(long number) {
        return StringUtil.length(StringUtil.valueOfUint64(number));
    }
    //endregion
}
