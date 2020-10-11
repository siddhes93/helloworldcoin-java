package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.core.utils.EncodeDecodeUtil;
import com.xingkaichun.helloworldblockchain.core.utils.LevelDBUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class MinerTransactionDtoDtoDataBaseDefaultImpl extends MinerTransactionDtoDataBase {

    private static final Logger logger = LoggerFactory.getLogger(MinerTransactionDtoDtoDataBaseDefaultImpl.class);

    private static final String MinerTransaction_DataBase_DirectName = "MinerTransactionDtoDataBase";
    private DB transactionPoolDB;

    public MinerTransactionDtoDtoDataBaseDefaultImpl(String blockchainDataPath) {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(blockchainDataPath,MinerTransaction_DataBase_DirectName));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LevelDBUtil.closeDB(transactionPoolDB);
        }));
    }

    public void insertTransactionDTO(TransactionDTO transactionDTO) {
        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (MinerTransactionDtoDtoDataBaseDefaultImpl.class){
            String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
            LevelDBUtil.put(transactionPoolDB,transactionHash, EncodeDecodeUtil.encode(transactionDTO));
        }
    }

    @Override
    public List<TransactionDTO> selectTransactionDtoList(long from, long size) {
        synchronized (MinerTransactionDtoDtoDataBaseDefaultImpl.class){
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
                    TransactionDTO transactionDTO = EncodeDecodeUtil.decodeToTransactionDTO(byteValue);
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
    public void deleteByTransactionHash(String transactionHash) {
        LevelDBUtil.delete(transactionPoolDB,transactionHash);
    }

    @Override
    public TransactionDTO selectTransactionDtoByTransactionHash(String transactionHash) {
        byte[] byteTransactionDTO = LevelDBUtil.get(transactionPoolDB,transactionHash);
        if(byteTransactionDTO == null){
            return null;
        }
        return EncodeDecodeUtil.decodeToTransactionDTO(byteTransactionDTO);
    }
}
