package com.helloworldcoin.core.tool;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionInput;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.transaction.TransactionType;
import com.helloworldcoin.setting.BlockSetting;
import com.helloworldcoin.util.LogUtil;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class StructureTool {


    /**
     * Check Block Structure
     */
    public static boolean checkBlockStructure(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            LogUtil.debug("Block data error: The number of transactions in the block is 0. A block must have a coinbase transaction.");
            return false;
        }
        //Check the number of transactions in the block
        long transactionCount = BlockTool.getTransactionCount(block);
        if(transactionCount > BlockSetting.BLOCK_MAX_TRANSACTION_COUNT){
            LogUtil.debug("Block data error: The number of transactions in the block exceeds the limit.");
            return false;
        }
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(i == 0){
                if(transaction.getTransactionType() != TransactionType.COINBASE_TRANSACTION){
                    LogUtil.debug("Block data error: The first transaction of the block must be a coinbase transaction.");
                    return false;
                }
            }else {
                if(transaction.getTransactionType() != TransactionType.STANDARD_TRANSACTION){
                    LogUtil.debug("Block data error: The non-first transaction of the block must be a standard transaction.");
                    return false;
                }
            }
        }
        //Check the structure of the transaction
        for(Transaction transaction:transactions){
            if(!checkTransactionStructure(transaction)){
                LogUtil.debug("Transaction data error: The transaction structure is abnormal.");
                return false;
            }
        }
        return true;
    }
    /**
     * Check Transaction Structure
     */
    public static boolean checkTransactionStructure(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.COINBASE_TRANSACTION){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                LogUtil.debug("Transaction data error: The coinbase transaction cannot have transaction input.");
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null || outputs.size()!=1){
                LogUtil.debug("Transaction data error: The coinbase transaction has one and only one transaction output.");
                return false;
            }
        }else if(transaction.getTransactionType() == TransactionType.STANDARD_TRANSACTION){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs == null || inputs.size()<1){
                LogUtil.debug("Transaction data error: The number of transaction inputs for a standard transaction is at least 1.");
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null || outputs.size()<1){
                LogUtil.debug("Transaction data error: The transaction output number of a standard transaction is at least 1.");
                return false;
            }
        }else {
            throw new RuntimeException();
        }
        //Check Transaction Input Script
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for (TransactionInput input:inputs) {
                //Strict Check: must be a P2PKH input script.
                if(!ScriptTool.isPayToPublicKeyHashInputScript(input.getInputScript())){
                    LogUtil.debug("Transaction data error: The transaction input script is not a P2PKH input script.");
                    return false;
                }
            }
        }
        //Check Transaction Output Script
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs) {
                //Strict Check: must be a P2PKH output script.
                if(!ScriptTool.isPayToPublicKeyHashOutputScript(output.getOutputScript())){
                    LogUtil.debug("Transaction data error: The transaction output script is not a P2PKH output script.");
                    return false;
                }
            }
        }
        return true;
    }
}
