package com.helloworldcoin.application.service;

import com.helloworldcoin.application.vo.block.BlockVo;
import com.helloworldcoin.application.vo.transaction.*;
import com.helloworldcoin.core.tool.BlockTool;
import com.helloworldcoin.core.tool.ScriptTool;
import com.helloworldcoin.core.tool.SizeTool;
import com.helloworldcoin.core.tool.TransactionTool;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;
import com.helloworldcoin.core.model.transaction.TransactionInput;
import com.helloworldcoin.core.model.transaction.TransactionOutput;
import com.helloworldcoin.netcore.BlockchainNetCore;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.StringUtil;
import com.helloworldcoin.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
@Service
public class BlockchainBrowserApplicationServiceImpl implements BlockchainBrowserApplicationService {

    @Autowired
    private BlockchainNetCore blockchainNetCore;



    @Override
    public TransactionOutputVo3 queryTransactionOutputByTransactionOutputId(String transactionHash, long transactionOutputIndex) {
        TransactionOutput transactionOutput = blockchainNetCore.getBlockchainCore().getBlockchainDatabase().queryTransactionOutputByTransactionOutputId(transactionHash,transactionOutputIndex);
        if(transactionOutput == null){
            return null;
        }

        TransactionOutputVo3 transactionOutputVo3 = new TransactionOutputVo3();
        transactionOutputVo3.setFromBlockHeight(transactionOutput.getBlockHeight());
        transactionOutputVo3.setFromBlockHash(transactionOutput.getBlockHash());
        transactionOutputVo3.setFromTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputVo3.setValue(transactionOutput.getValue());
        transactionOutputVo3.setFromOutputScript(ScriptTool.outputScript2String(transactionOutput.getOutputScript()));
        transactionOutputVo3.setFromTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());

        TransactionOutput transactionOutputTemp = blockchainNetCore.getBlockchainCore().getBlockchainDatabase().queryUnspentTransactionOutputByTransactionOutputId(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
        transactionOutputVo3.setUnspentTransactionOutput(transactionOutputTemp!=null);

        TransactionVo inputTransactionVo = queryTransactionByTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputVo3.setInputTransaction(inputTransactionVo);
        transactionOutputVo3.setTransactionType(inputTransactionVo.getTransactionType());


        TransactionVo outputTransactionVo;
        if(transactionOutputTemp==null){
            Transaction destinationTransaction = blockchainNetCore.getBlockchainCore().getBlockchainDatabase().queryDestinationTransactionByTransactionOutputId(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
            List<TransactionInput> inputs = destinationTransaction.getInputs();
            if(inputs != null){
                for(int inputIndex=0; inputIndex<inputs.size(); inputIndex++){
                    TransactionInput transactionInput = inputs.get(inputIndex);
                    TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                    if(StringUtil.equals(transactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionHash()) &&
                            transactionOutput.getTransactionOutputIndex()==unspentTransactionOutput.getTransactionOutputIndex()){
                        transactionOutputVo3.setToTransactionInputIndex(inputIndex+1);
                        transactionOutputVo3.setToInputScript(ScriptTool.inputScript2String(transactionInput.getInputScript()));
                        break;
                    }
                }
            }
            outputTransactionVo = queryTransactionByTransactionHash(destinationTransaction.getTransactionHash());
            transactionOutputVo3.setToBlockHeight(outputTransactionVo.getBlockHeight());
            transactionOutputVo3.setToBlockHash(outputTransactionVo.getBlockHash());
            transactionOutputVo3.setToTransactionHash(outputTransactionVo.getTransactionHash());
            transactionOutputVo3.setOutputTransaction(outputTransactionVo);
        }
        return transactionOutputVo3;
    }

    @Override
    public TransactionOutputVo3 queryTransactionOutputByAddress(String address) {
        TransactionOutput transactionOutput = blockchainNetCore.getBlockchainCore().queryTransactionOutputByAddress(address);
        if(transactionOutput == null){
            return null;
        }
        TransactionOutputVo3 transactionOutputVo3 = queryTransactionOutputByTransactionOutputId(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
        return transactionOutputVo3;
    }

    @Override
    public List<TransactionVo> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size) {
        Block block = blockchainNetCore.getBlockchainCore().queryBlockByBlockHash(blockHash);
        if(block == null){
            return null;
        }
        List<TransactionVo> transactionVos = new ArrayList<>();
        for(long i=from; i<from+size; i++){
            if(from < 0){
                break;
            }
            if(i > block.getTransactionCount()){
                break;
            }
            long transactionHeight = block.getPreviousTransactionHeight() + i;
            Transaction transaction = blockchainNetCore.getBlockchainCore().queryTransactionByTransactionHeight(transactionHeight);
            TransactionVo transactionVo = queryTransactionByTransactionHash(transaction.getTransactionHash());
            transactionVos.add(transactionVo);
        }
        return transactionVos;
    }

    @Override
    public BlockVo queryBlockViewByBlockHeight(long blockHeight) {
        Block block = blockchainNetCore.getBlockchainCore().queryBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        long blockchainHeight = blockchainNetCore.getBlockchainCore().queryBlockchainHeight();
        Block nextBlock = blockchainNetCore.getBlockchainCore().queryBlockByBlockHeight(block.getHeight()+1);

        BlockVo blockVo = new BlockVo();
        blockVo.setHeight(block.getHeight());
        blockVo.setBlockConfirmations(blockchainHeight-block.getHeight()+1);
        blockVo.setBlockSize(SizeTool.calculateBlockSize(block));
        blockVo.setTransactionCount(BlockTool.getTransactionCount(block));
        blockVo.setTime(TimeUtil.formatMillisecondTimestamp(block.getTimestamp()));
        blockVo.setMinerIncentiveValue(BlockTool.getWritedIncentiveValue(block));
        blockVo.setDifficulty(BlockTool.formatDifficulty(block.getDifficulty()));
        blockVo.setNonce(block.getNonce());
        blockVo.setHash(block.getHash());
        blockVo.setPreviousBlockHash(block.getPreviousHash());
        blockVo.setNextBlockHash(nextBlock==null?null:nextBlock.getHash());
        blockVo.setMerkleTreeRoot(block.getMerkleTreeRoot());
        return blockVo;
    }

    @Override
    public UnconfirmedTransactionVo queryUnconfirmedTransactionByTransactionHash(String transactionHash) {
        try {
            TransactionDto transactionDto = blockchainNetCore.getBlockchainCore().queryUnconfirmedTransactionByTransactionHash(transactionHash);
            if(transactionDto == null){
                return null;
            }
            Transaction transaction = blockchainNetCore.getBlockchainCore().getBlockchainDatabase().transactionDto2Transaction(transactionDto);
            UnconfirmedTransactionVo transactionDtoVo = new UnconfirmedTransactionVo();
            transactionDtoVo.setTransactionHash(transaction.getTransactionHash());

            List<TransactionInputVo2> inputDtos = new ArrayList<>();
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for(TransactionInput input:inputs){
                    TransactionInputVo2 transactionInputVo = new TransactionInputVo2();
                    transactionInputVo.setAddress(input.getUnspentTransactionOutput().getAddress());
                    transactionInputVo.setTransactionHash(input.getUnspentTransactionOutput().getTransactionHash());
                    transactionInputVo.setTransactionOutputIndex(input.getUnspentTransactionOutput().getTransactionOutputIndex());
                    transactionInputVo.setValue(input.getUnspentTransactionOutput().getValue());
                    inputDtos.add(transactionInputVo);
                }
            }
            transactionDtoVo.setTransactionInputs(inputDtos);

            List<TransactionOutputVo2> outputDtos = new ArrayList<>();
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for(TransactionOutput output:outputs){
                    TransactionOutputVo2 transactionOutputVo = new TransactionOutputVo2();
                    transactionOutputVo.setAddress(output.getAddress());
                    transactionOutputVo.setValue(output.getValue());
                    outputDtos.add(transactionOutputVo);
                }
            }
            transactionDtoVo.setTransactionOutputs(outputDtos);

            return transactionDtoVo;
        }catch (Exception e){
            LogUtil.error("'query unconfirmed transaction by transaction hash' error.",e);
            return null;
        }
    }


    @Override
    public TransactionVo queryTransactionByTransactionHash(String transactionHash) {
        Transaction transaction = blockchainNetCore.getBlockchainCore().queryTransactionByTransactionHash(transactionHash);
        if(transaction == null){
            return null;
        }

        TransactionVo transactionVo = new TransactionVo();
        transactionVo.setTransactionHash(transaction.getTransactionHash());
        transactionVo.setBlockHeight(transaction.getBlockHeight());

        transactionVo.setTransactionFee(TransactionTool.getTransactionFee(transaction));
        transactionVo.setTransactionType(transaction.getTransactionType().name());
        transactionVo.setTransactionInputCount(TransactionTool.getTransactionInputCount(transaction));
        transactionVo.setTransactionOutputCount(TransactionTool.getTransactionOutputCount(transaction));
        transactionVo.setTransactionInputValues(TransactionTool.getInputValue(transaction));
        transactionVo.setTransactionOutputValues(TransactionTool.getOutputValue(transaction));

        long blockchainHeight = blockchainNetCore.getBlockchainCore().queryBlockchainHeight();
        Block block = blockchainNetCore.getBlockchainCore().queryBlockByBlockHeight(transaction.getBlockHeight());
        transactionVo.setBlockConfirmations(blockchainHeight-block.getHeight()+1);
        transactionVo.setBlockTime(TimeUtil.formatMillisecondTimestamp(block.getTimestamp()));
        transactionVo.setBlockHash(block.getHash());

        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionInputVo> transactionInputVos = new ArrayList<>();
        if(inputs != null){
            for(TransactionInput transactionInput:inputs){
                TransactionInputVo transactionInputVo = new TransactionInputVo();
                transactionInputVo.setAddress(transactionInput.getUnspentTransactionOutput().getAddress());
                transactionInputVo.setValue(transactionInput.getUnspentTransactionOutput().getValue());
                transactionInputVo.setInputScript(ScriptTool.inputScript2String(transactionInput.getInputScript()));
                transactionInputVo.setTransactionHash(transactionInput.getUnspentTransactionOutput().getTransactionHash());
                transactionInputVo.setTransactionOutputIndex(transactionInput.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputVos.add(transactionInputVo);
            }
        }
        transactionVo.setTransactionInputs(transactionInputVos);

        List<TransactionOutput> outputs = transaction.getOutputs();
        List<TransactionOutputVo> transactionOutputVos = new ArrayList<>();
        if(outputs != null){
            for(TransactionOutput transactionOutput:outputs){
                TransactionOutputVo transactionOutputVo = new TransactionOutputVo();
                transactionOutputVo.setAddress(transactionOutput.getAddress());
                transactionOutputVo.setValue(transactionOutput.getValue());
                transactionOutputVo.setOutputScript(ScriptTool.outputScript2String(transactionOutput.getOutputScript()));
                transactionOutputVo.setTransactionHash(transactionOutput.getTransactionHash());
                transactionOutputVo.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
                transactionOutputVos.add(transactionOutputVo);
            }
        }
        transactionVo.setTransactionOutputs(transactionOutputVos);

        List<String> inputScripts = new ArrayList<>();
        for (TransactionInputVo transactionInputVo : transactionInputVos){
            inputScripts.add(transactionInputVo.getInputScript());
        }
        transactionVo.setInputScripts(inputScripts);

        List<String> outputScripts = new ArrayList<>();
        for (TransactionOutputVo transactionOutputVo : transactionOutputVos){
            outputScripts.add(transactionOutputVo.getOutputScript());
        }
        transactionVo.setOutputScripts(outputScripts);

        return transactionVo;
    }
}
