package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.UnconfirmedTransactionDatabase;
import com.xingkaichun.helloworldblockchain.core.tool.TransactionDtoTool;
import com.xingkaichun.helloworldblockchain.util.*;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class UnconfirmedTransactionDatabaseDefaultImpl extends UnconfirmedTransactionDatabase {

    private static final String UNCONFIRMED_TRANSACTION_DATABASE_NAME = "UnconfirmedTransactionDatabase";

    public UnconfirmedTransactionDatabaseDefaultImpl(CoreConfiguration coreConfiguration) {
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    public boolean insertTransaction(TransactionDto transaction) {
        try {
            String transactionHash = TransactionDtoTool.calculateTransactionHash(transaction);
            KvDbUtil.put(getUnconfirmedTransactionDatabasePath(), getKey(transactionHash), EncodeDecodeTool.encode(transaction));
            return true;
        }catch (Exception e){
            LogUtil.error("交易["+ JsonUtil.toString(transaction)+"]放入交易池异常。",e);
            return false;
        }
    }

    @Override
    public List<TransactionDto> selectTransactions(long from, long size) {
        List<TransactionDto> transactionDtos = new ArrayList<>();
        List<byte[]> bytesTransactionDtos = KvDbUtil.gets(getUnconfirmedTransactionDatabasePath(),from,size);
        if(bytesTransactionDtos != null){
            for(byte[] bytesTransactionDto:bytesTransactionDtos){
                TransactionDto transactionDto = EncodeDecodeTool.decode(bytesTransactionDto,TransactionDto.class);
                transactionDtos.add(transactionDto);
            }
        }
        return transactionDtos;
    }

    @Override
    public void deleteByTransactionHash(String transactionHash) {
        KvDbUtil.delete(getUnconfirmedTransactionDatabasePath(), getKey(transactionHash));
    }

    @Override
    public TransactionDto selectTransactionByTransactionHash(String transactionHash) {
        byte[] byteTransactionDto = KvDbUtil.get(getUnconfirmedTransactionDatabasePath(), getKey(transactionHash));
        if(byteTransactionDto == null){
            return null;
        }
        return EncodeDecodeTool.decode(byteTransactionDto,TransactionDto.class);
    }

    private String getUnconfirmedTransactionDatabasePath() {
        return FileUtil.newPath(coreConfiguration.getCorePath(), UNCONFIRMED_TRANSACTION_DATABASE_NAME);
    }

    private byte[] getKey(String transactionHash){
        return ByteUtil.hexStringToBytes(transactionHash);
    }
}
