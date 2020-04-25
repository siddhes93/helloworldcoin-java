package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.TransactionDataBase;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.LevelDBUtil;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MinerTransactionDtoDtoDataBaseDefaultImpl extends MinerTransactionDtoDataBase {

    private Logger logger = LoggerFactory.getLogger(MinerTransactionDtoDtoDataBaseDefaultImpl.class);

    private final static String MinerTransaction_DataBase_DirectName = "MinerTransactionDtoDataBase";
    private DB transactionPoolDB;
    private TransactionDataBase transactionDataBase;

    public MinerTransactionDtoDtoDataBaseDefaultImpl(String blockchainDataPath, TransactionDataBase transactionDataBase) throws Exception {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(blockchainDataPath,MinerTransaction_DataBase_DirectName));
        this.transactionDataBase = transactionDataBase;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                transactionPoolDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void insertTransactionDTO(TransactionDTO transactionDTO) throws Exception {

        //校验签名 防止签名错误的交易加入交易池
        transactionDataBase.insertTransaction(transactionDTO);

        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (BlockChainDataBase.class){
            LevelDBUtil.put(transactionPoolDB,transactionDTO.getTransactionHash(), encode(transactionDTO));
        }
    }

    @Override
    public void insertTransactionDtoList(List<TransactionDTO> transactionDTOList) throws Exception {
        WriteBatch writeBatch = new WriteBatchImpl();
        for(TransactionDTO transactionDTO:transactionDTOList){
            writeBatch.put(LevelDBUtil.stringToBytes(transactionDTO.getTransactionHash()),encode(transactionDTO));
        }
        synchronized (BlockChainDataBase.class){
            LevelDBUtil.write(transactionPoolDB, writeBatch);
        }
    }

    @Override
    public List<TransactionDTO> selectTransactionDtoList(BlockChainDataBase blockChainDataBase,long from, long size) throws Exception {
        synchronized (BlockChainDataBase.class){
            List<TransactionDTO> transactionDtoList = new ArrayList<>();
            int cunrrentFrom = 0;
            int cunrrentSize = 0;
            for (DBIterator iterator = this.transactionPoolDB.iterator(); iterator.hasNext(); iterator.next()) {
                byte[] byteValue = iterator.peekNext().getValue();
                if(byteValue == null || byteValue.length==0){
                    continue;
                }
                cunrrentFrom++;
                if(cunrrentFrom>=from && cunrrentSize<size){
                    TransactionDTO transactionDTO = decodeToTransactionDTO(byteValue);
                    transactionDtoList.add(transactionDTO);
                    cunrrentSize++;
                }
                if(cunrrentSize>=size){
                    break;
                }
            }
            return transactionDtoList;
        }
    }

    @Override
    public void deleteTransactionDtoByTransactionHash(String transactionHash) throws Exception {
        LevelDBUtil.delete(transactionPoolDB,transactionHash);
    }

    @Override
    public void deleteTransactionDtoListByTransactionHashList(List<String> transactionHashList) throws Exception {
        if(transactionHashList == null || transactionHashList.size()==0){
            return;
        }
        WriteBatch writeBatch = new WriteBatchImpl();
        for(String transactionHash:transactionHashList){
            writeBatch.delete(LevelDBUtil.stringToBytes(transactionHash));
        }
        LevelDBUtil.write(transactionPoolDB,writeBatch);
    }

    @Override
    public TransactionDTO selectTransactionDtoByTransactionHash(String transactionHash) throws Exception {
        byte[] byteTransactionDTO = LevelDBUtil.get(transactionPoolDB,transactionHash);
        if(byteTransactionDTO == null){
            return null;
        }
        return decodeToTransactionDTO(byteTransactionDTO);
    }

    private static byte[] encode(TransactionDTO transactionDTO) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(transactionDTO);
        byte[] bytesTransactionDTO = byteArrayOutputStream.toByteArray();
        return bytesTransactionDTO;
    }

    private static TransactionDTO decodeToTransactionDTO(byte[] bytesTransactionDTO) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransactionDTO);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        TransactionDTO transactionDTO = (TransactionDTO) objectInputStream.readObject();
        return transactionDTO;
    }
}
