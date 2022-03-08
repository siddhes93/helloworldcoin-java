package com.helloworldcoin.core.tool;

import com.helloworldcoin.crypto.AccountUtil;
import com.helloworldcoin.crypto.Sha256Util;
import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class TransactionDtoTool {

    public static String calculateTransactionHash(TransactionDto transactionDto){
        byte[] bytesTransaction = bytesTransaction(transactionDto,false);
        byte[] bytesTransactionHash = Sha256Util.doubleDigest(bytesTransaction);
        return ByteUtil.bytesToHexString(bytesTransactionHash);
    }

    public static String getSignatureHashAllRawMaterial(TransactionDto transactionDto) {
        byte[] bytesTransaction = bytesTransaction(transactionDto,true);
        byte[] sha256Digest = Sha256Util.doubleDigest(bytesTransaction);
        return ByteUtil.bytesToHexString(sha256Digest);
    }

    public static String signature(String privateKey, TransactionDto transactionDto) {
        String message = getSignatureHashAllRawMaterial(transactionDto);
        byte[] bytesMessage = ByteUtil.hexStringToBytes(message);
        String signature = AccountUtil.signature(privateKey,bytesMessage);
        return signature;
    }

    public static boolean verifySignature(TransactionDto transaction, String publicKey, byte[] bytesSignature) {
        String message = getSignatureHashAllRawMaterial(transaction);
        byte[] bytesMessage = ByteUtil.hexStringToBytes(message);
        return AccountUtil.verifySignature(publicKey,bytesMessage,bytesSignature);
    }

    //region Serialization and Deserialization
    /**
     * Serialization: Convert TransactionDto into byte array. Requires that the resulting byte array can Convert into the original transaction.
     */
    public static byte[] bytesTransaction(TransactionDto transactionDto, boolean omitInputScript) {
        List<byte[]> bytesUnspentTransactionOutputs = new ArrayList<>();
        List<TransactionInputDto> inputs = transactionDto.getInputs();
        if(inputs != null){
            for(TransactionInputDto transactionInputDto:inputs){
                byte[] bytesUnspentTransactionOutput;
                if(omitInputScript){
                    byte[] bytesTransactionHash = ByteUtil.hexStringToBytes(transactionInputDto.getTransactionHash());
                    byte[] bytesTransactionOutputIndex = ByteUtil.uint64ToBytes(transactionInputDto.getTransactionOutputIndex());
                    bytesUnspentTransactionOutput = ByteUtil.concatenate(ByteUtil.concatenateLength(bytesTransactionHash),
                            ByteUtil.concatenateLength(bytesTransactionOutputIndex));
                }else {
                    byte[] bytesTransactionHash = ByteUtil.hexStringToBytes(transactionInputDto.getTransactionHash());
                    byte[] bytesTransactionOutputIndex = ByteUtil.uint64ToBytes(transactionInputDto.getTransactionOutputIndex());
                    byte[] bytesInputScript = ScriptDtoTool.inputScript2Bytes(transactionInputDto.getInputScript());
                    bytesUnspentTransactionOutput = ByteUtil.concatenate3(ByteUtil.concatenateLength(bytesTransactionHash),
                            ByteUtil.concatenateLength(bytesTransactionOutputIndex),ByteUtil.concatenateLength(bytesInputScript));
                }
                bytesUnspentTransactionOutputs.add(ByteUtil.concatenateLength(bytesUnspentTransactionOutput));
            }
        }

        List<byte[]> bytesTransactionOutputs = new ArrayList<>();
        List<TransactionOutputDto> outputs = transactionDto.getOutputs();
        if(outputs != null){
            for(TransactionOutputDto transactionOutputDto:outputs){
                byte[] bytesOutputScript = ScriptDtoTool.outputScript2Bytes(transactionOutputDto.getOutputScript());
                byte[] bytesValue = ByteUtil.uint64ToBytes(transactionOutputDto.getValue());
                byte[] bytesTransactionOutput = ByteUtil.concatenate(ByteUtil.concatenateLength(bytesOutputScript),ByteUtil.concatenateLength(bytesValue));
                bytesTransactionOutputs.add(ByteUtil.concatenateLength(bytesTransactionOutput));
            }
        }

        byte[] data = ByteUtil.concatenate(ByteUtil.flatAndConcatenateLength(bytesUnspentTransactionOutputs),
                ByteUtil.flatAndConcatenateLength(bytesTransactionOutputs));
        return data;
    }
    /**
     * Deserialization: Convert byte array into TransactionDto.
     */
    public static TransactionDto transactionDto(byte[] bytesTransaction, boolean omitInputScript) {
        TransactionDto transactionDto = new TransactionDto();
        int start = 0;
        long bytesTransactionInputDtosLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransaction,start,start + ByteUtil.BYTE8_BYTE_COUNT));
        start += ByteUtil.BYTE8_BYTE_COUNT;
        byte[] bytesTransactionInputDtos = ByteUtil.copy(bytesTransaction,start, start+(int) bytesTransactionInputDtosLength);
        start += bytesTransactionInputDtosLength;
        List<TransactionInputDto> transactionInputDtos = transactionInputDtos(bytesTransactionInputDtos,omitInputScript);
        transactionDto.setInputs(transactionInputDtos);

        long bytesTransactionOutputsLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransaction,start,start + ByteUtil.BYTE8_BYTE_COUNT));
        start += ByteUtil.BYTE8_BYTE_COUNT;
        byte[] bytesTransactionOutputs = ByteUtil.copy(bytesTransaction,start, start+(int) bytesTransactionOutputsLength);
        start += bytesTransactionOutputsLength;
        List<TransactionOutputDto> transactionOutputDtos = transactionOutputDtos(bytesTransactionOutputs);
        transactionDto.setOutputs(transactionOutputDtos);
        return transactionDto;
    }
    private static List<TransactionOutputDto> transactionOutputDtos(byte[] bytesTransactionOutputs) {
        if(bytesTransactionOutputs == null || bytesTransactionOutputs.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionOutputDto> transactionOutputDtos = new ArrayList<>();
        while (start < bytesTransactionOutputs.length){
            long bytesTransactionOutputDtoLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionOutputs,start,start + ByteUtil.BYTE8_BYTE_COUNT));
            start += ByteUtil.BYTE8_BYTE_COUNT;
            byte[] bytesTransactionOutput = ByteUtil.copy(bytesTransactionOutputs,start, start+(int) bytesTransactionOutputDtoLength);
            start += bytesTransactionOutputDtoLength;
            TransactionOutputDto transactionOutputDto = transactionOutputDto(bytesTransactionOutput);
            transactionOutputDtos.add(transactionOutputDto);
            if(start >= bytesTransactionOutputs.length){
                break;
            }
        }
        return transactionOutputDtos;
    }
    private static TransactionOutputDto transactionOutputDto(byte[] bytesTransactionOutput) {
        int start = 0;
        long bytesOutputScriptLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionOutput,start,start + ByteUtil.BYTE8_BYTE_COUNT));
        start += ByteUtil.BYTE8_BYTE_COUNT;
        byte[] bytesOutputScript = ByteUtil.copy(bytesTransactionOutput,start, start+(int) bytesOutputScriptLength);
        start += bytesOutputScriptLength;
        OutputScriptDto outputScriptDto = ScriptDtoTool.bytes2OutputScript(bytesOutputScript);

        long bytesValueLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionOutput,start,start + ByteUtil.BYTE8_BYTE_COUNT));
        start += ByteUtil.BYTE8_BYTE_COUNT;
        byte[] bytesValue = ByteUtil.copy(bytesTransactionOutput,start, start+(int) bytesValueLength);
        start += bytesValueLength;

        TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
        transactionOutputDto.setOutputScript(outputScriptDto);
        transactionOutputDto.setValue(ByteUtil.bytesToUint64(bytesValue));
        return transactionOutputDto;
    }
    private static List<TransactionInputDto> transactionInputDtos(byte[] bytesTransactionInputDtos, boolean omitInputScript) {
        if(bytesTransactionInputDtos == null || bytesTransactionInputDtos.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionInputDto> transactionInputDtos = new ArrayList<>();
        while (start < bytesTransactionInputDtos.length){
            long bytesTransactionInputDtoLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionInputDtos,start,start + ByteUtil.BYTE8_BYTE_COUNT));
            start += ByteUtil.BYTE8_BYTE_COUNT;
            byte[] bytesTransactionInput = ByteUtil.copy(bytesTransactionInputDtos,start, start+(int) bytesTransactionInputDtoLength);
            start += bytesTransactionInputDtoLength;
            TransactionInputDto transactionInputDto = transactionInputDto(bytesTransactionInput,omitInputScript);
            transactionInputDtos.add(transactionInputDto);
            if(start >= bytesTransactionInputDtos.length){
                break;
            }
        }
        return transactionInputDtos;
    }
    private static TransactionInputDto transactionInputDto(byte[] bytesTransactionInputDto, boolean omitInputScript) {
        int start = 0;
        long bytesTransactionHashLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionInputDto,start,start + ByteUtil.BYTE8_BYTE_COUNT));
        start += ByteUtil.BYTE8_BYTE_COUNT;
        byte[] bytesTransactionHash = ByteUtil.copy(bytesTransactionInputDto,start, start+(int) bytesTransactionHashLength);
        start += bytesTransactionHashLength;

        long bytesTransactionOutputIndexLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionInputDto,start,start + ByteUtil.BYTE8_BYTE_COUNT));
        start += ByteUtil.BYTE8_BYTE_COUNT;
        byte[] bytesTransactionOutputIndex = ByteUtil.copy(bytesTransactionInputDto,start, start+(int) bytesTransactionOutputIndexLength);
        start += bytesTransactionOutputIndexLength;

        TransactionInputDto transactionInputDto = new TransactionInputDto();
        if(!omitInputScript){
            long bytesOutputScriptLength = ByteUtil.bytesToUint64(ByteUtil.copy(bytesTransactionInputDto,start,start + ByteUtil.BYTE8_BYTE_COUNT));
            start += ByteUtil.BYTE8_BYTE_COUNT;
            byte[] bytesOutputScript = ByteUtil.copy(bytesTransactionInputDto,start, start+(int) bytesOutputScriptLength);
            start += bytesOutputScriptLength;
            InputScriptDto inputScriptDto = ScriptDtoTool.bytes2InputScript(bytesOutputScript);
            transactionInputDto.setInputScript(inputScriptDto);
        }
        transactionInputDto.setTransactionHash(ByteUtil.bytesToHexString(bytesTransactionHash));
        transactionInputDto.setTransactionOutputIndex(ByteUtil.bytesToUint64(bytesTransactionOutputIndex));
        return transactionInputDto;
    }
    //endregion
}
