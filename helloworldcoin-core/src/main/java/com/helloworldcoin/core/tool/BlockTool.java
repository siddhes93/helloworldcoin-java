package com.helloworldcoin.core.tool;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionInput;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.core.model.transaction.TransactionType;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.setting.GenesisBlockSetting;
import com.helloworldcoin.util.StringUtil;
import com.helloworldcoin.util.StringsUtil;
import com.helloworldcoin.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockTool {

    public static String calculateBlockHash(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return BlockDtoTool.calculateBlockHash(blockDto);
    }

    public static String calculateBlockMerkleTreeRoot(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return BlockDtoTool.calculateBlockMerkleTreeRoot(blockDto);
    }

    public static boolean isExistDuplicateNewHash(Block block) {
        List<String> newHashs = new ArrayList<>();
        String blockHash = block.getHash();
        newHashs.add(blockHash);
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction : transactions){
                String transactionHash = transaction.getTransactionHash();
                newHashs.add(transactionHash);
            }
        }
        return StringsUtil.hasDuplicateElement(newHashs);
    }

    public static boolean isExistDuplicateNewAddress(Block block) {
        List<String> newAddresss = new ArrayList<>();
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction : transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (TransactionOutput output:outputs){
                        String address = output.getAddress();
                        newAddresss.add(address);
                    }
                }
            }
        }
        return StringsUtil.hasDuplicateElement(newAddresss);
    }

    public static boolean isExistDuplicateUtxo(Block block) {
        List<String> utxoIds = new ArrayList<>();
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null) {
            for(Transaction transaction : transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput : inputs) {
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        String utxoId = TransactionTool.getTransactionOutputId(unspentTransactionOutput);
                        utxoIds.add(utxoId);
                    }
                }
            }
        }
        return StringsUtil.hasDuplicateElement(utxoIds);
    }

    public static boolean checkPreviousBlockHash(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return StringUtil.equals(GenesisBlockSetting.HASH,currentBlock.getPreviousHash());
        } else {
            return StringUtil.equals(previousBlock.getHash(),currentBlock.getPreviousHash());
        }
    }

    public static boolean checkBlockHeight(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return (GenesisBlockSetting.HEIGHT +1) == currentBlock.getHeight();
        } else {
            return (previousBlock.getHeight()+1) == currentBlock.getHeight();
        }
    }

    public static boolean checkBlockTimestamp(Block previousBlock, Block currentBlock) {
        if(currentBlock.getTimestamp() > TimeUtil.millisecondTimestamp()){
            return false;
        }
        if(previousBlock == null){
            return true;
        } else {
            return currentBlock.getTimestamp() > previousBlock.getTimestamp();
        }
    }

    public static long getTransactionCount(Block block) {
        List<Transaction> transactions = block.getTransactions();
        return transactions == null?0:transactions.size();
    }

    public static boolean isBlockEquals(Block block1, Block block2) {
        return StringUtil.equals(block1.getHash(), block2.getHash());
    }

    public static long getWritedIncentiveValue(Block block) {
        return block.getTransactions().get(0).getOutputs().get(0).getValue();
    }

    public static String formatDifficulty(String difficulty) {
        return StringUtil.prefixPadding(difficulty,64,"0");
    }

    public static long getTransactionOutputCount(Block block) {
        long transactionOutputCount = 0;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                transactionOutputCount += TransactionTool.getTransactionOutputCount(transaction);
            }
        }
        return transactionOutputCount;
    }

    public static long getBlockFee(Block block) {
        long blockFee = 0;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(transaction.getTransactionType() == TransactionType.COINBASE_TRANSACTION){
                    continue;
                }else if(transaction.getTransactionType() == TransactionType.STANDARD_TRANSACTION){
                    long fee = TransactionTool.getTransactionFee(transaction);
                    blockFee += fee;
                }else{
                    throw new RuntimeException();
                }
            }
        }
        return blockFee;
    }

    public static long getNextBlockHeight(Block currentBlock) {
        long nextBlockHeight = currentBlock==null? GenesisBlockSetting.HEIGHT+1:currentBlock.getHeight()+1;
        return nextBlockHeight;
    }
}
