package com.helloworldcoin.core.tool;


import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionInput;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.transaction.TransactionType;
import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.StringsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionTool {

    /**
     * Get Total Input Value Of Transaction
     */
    public static long getInputValue(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        long total = 0;
        if(inputs != null){
            for(TransactionInput input : inputs) {
                total += input.getUnspentTransactionOutput().getValue();
            }
        }
        return total;
    }
    /**
     * Get Total Output Value Of Transaction
     */
    public static long getOutputValue(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        long total = 0;
        if(outputs != null){
            for(TransactionOutput output : outputs) {
                total += output.getValue();
            }
        }
        return total;
    }
    /**
     * Get Total Fees Of Transaction
     */
    public static long getTransactionFee(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.STANDARD_TRANSACTION){
            long inputsValue = getInputValue(transaction);
            long outputsValue = getOutputValue(transaction);
            long transactionFee = inputsValue - outputsValue;
            return transactionFee;
        }else if(transaction.getTransactionType() == TransactionType.COINBASE_TRANSACTION){
            return 0L;
        }else{
            throw new RuntimeException();
        }
    }
    /**
     * Get Fee Rate Of Transaction
     */
    public static long getTransactionFeeRate(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.STANDARD_TRANSACTION){
            return getTransactionFee(transaction)/SizeTool.calculateTransactionSize(transaction);
        }else if(transaction.getTransactionType() == TransactionType.COINBASE_TRANSACTION){
            return 0L;
        }else {
            throw new RuntimeException();
        }
    }




    public static String getSignatureHashAllRawMaterial(Transaction transaction) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.getSignatureHashAllRawMaterial(transactionDto);
    }

    public static String signature(String privateKey, Transaction transaction) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.signature(privateKey,transactionDto);
    }

    public static boolean verifySignature(Transaction transaction, String publicKey, byte[] bytesSignature) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return  TransactionDtoTool.verifySignature(transactionDto,publicKey,bytesSignature);
    }




    public static String calculateTransactionHash(Transaction transaction){
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.calculateTransactionHash(transactionDto);
    }
    public static long getTransactionInputCount(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        long transactionInputCount = inputs==null?0:inputs.size();
        return transactionInputCount;
    }
    public static long getTransactionOutputCount(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        long transactionOutputCount = outputs==null?0:outputs.size();
        return transactionOutputCount;
    }
    public static String getTransactionOutputId(TransactionOutput transactionOutput) {
        return BlockchainDatabaseKeyTool.buildTransactionOutputId(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
    }




    /**
     * Check Transaction Value
     */
    public static boolean checkTransactionValue(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            //Check Transaction Input Value
            for(TransactionInput input:inputs){
                if(!checkValue(input.getUnspentTransactionOutput().getValue())){
                    LogUtil.debug("Transaction value is illegal.");
                    return false;
                }
            }
        }

        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            //Check Transaction Output Value
            for(TransactionOutput output:outputs){
                if(!checkValue(output.getValue())){
                    LogUtil.debug("Transaction value is illegal.");
                    return false;
                }
            }
        }

        //further check by transaction type
        if(transaction.getTransactionType() == TransactionType.COINBASE_TRANSACTION){
            //There is no need to check, skip.
        } else if(transaction.getTransactionType() == TransactionType.STANDARD_TRANSACTION){
            //The transaction input value must be greater than or equal to the transaction output value
            long inputsValue = getInputValue(transaction);
            long outputsValue = getOutputValue(transaction);
            if(inputsValue < outputsValue) {
                LogUtil.debug("Transaction value is illegal.");
                return false;
            }
            return true;
        } else {
            throw new RuntimeException();
        }
        return true;
    }
    /**
     * Check whether the transaction value is legal: this is used to limit the maximum value, minimum value, decimal places, etc. of the transaction value
     */
    public static boolean checkValue(long transactionAmount) {
        //The transaction value cannot be less than or equal to 0
        if(transactionAmount <= 0){
            return false;
        }
        //The maximum value is 2^64
        //The reserved decimal place is 0
        return true;
    }
    /**
     * Check if the address in the transaction is a P2PKH address
     */
    public static boolean checkPayToPublicKeyHashAddress(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                if(!AccountUtil.isPayToPublicKeyHashAddress(output.getAddress())){
                    LogUtil.debug("Transaction address is illegal.");
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Check if the script in the transaction is a P2PKH script
     */
    public static boolean checkPayToPublicKeyHashScript(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput input:inputs){
                if(!ScriptTool.isPayToPublicKeyHashInputScript(input.getInputScript())){
                    return false;
                }
            }
        }
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs) {
                if(!ScriptTool.isPayToPublicKeyHashOutputScript(output.getOutputScript())){
                    return false;
                }
            }
        }
        return true;
    }




    /**
     * Is there a duplicate [unspent transaction output] in the transaction
     */
    public static boolean isExistDuplicateUtxo(Transaction transaction) {
        List<String> utxoIds = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                String utxoId = getTransactionOutputId(unspentTransactionOutput);
                utxoIds.add(utxoId);
            }
        }
        return StringsUtil.hasDuplicateElement(utxoIds);
    }
    /**
     * Whether the newly generated address of the block is duplicated
     */
    public static boolean isExistDuplicateNewAddress(Transaction transaction) {
        List<String> newAddresss = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                newAddresss.add(address);
            }
        }
        return StringsUtil.hasDuplicateElement(newAddresss);
    }
    /**
     * Sort transactions by rate (fee per character) from largest to smallest
     */
    public static void sortByTransactionFeeRateDescend(List<Transaction> transactions) {
        if(transactions == null){
            return;
        }
        transactions.sort((transaction1, transaction2) -> {
            long transaction1FeeRate = TransactionTool.getTransactionFeeRate(transaction1);
            long transaction2FeeRate = TransactionTool.getTransactionFeeRate(transaction2);
            long diffFeeRate = transaction1FeeRate - transaction2FeeRate;
            if(diffFeeRate>0){
                return -1;
            }else if(diffFeeRate==0){
                return 0;
            }else {
                return 1;
            }
        });
    }
}
