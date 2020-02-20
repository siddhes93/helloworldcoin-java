package com.xingkaichun.blockchain.core;

import com.google.gson.Gson;
import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

public class TranslateTest {

    @org.junit.Test
    public void test() throws Exception {
        String dbPath = "D:\\logs\\hellowordblockchain\\" ;
        String minerPublicKeyString = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwpbppp/kd7di7NXVcxyTPd4bcpm9ZQArbyMV24veV4fzDnGspPNPGh9530GnhPycGiEKGLDNchTiyQ5+zWTlA==" ;

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl(dbPath+"BlockChainDataBase",incentive,consensus);

        TransactionDataBase transactionDataBase = new TransactionDataBaseDefaultImpl();

        MinerTransactionDtoDataBase minerTransactionDtoDataBase = new MinerTransactionDtoDtoDataBaseDefaultImpl(dbPath+"MinerTransactionDtoDataBase",transactionDataBase);
        PublicKeyString minerPublicKey = new PublicKeyString(minerPublicKeyString);
        Miner miner = new MinerDefaultImpl(blockChainDataBase, minerTransactionDtoDataBase,minerPublicKey);

        SynchronizerDataBase synchronizerDataBase = new SynchronizerDataBaseDefaultImpl(dbPath+"Synchronizer","otherNodeBlock.data",transactionDataBase);
        BlockChainDataBase blockChainDataBaseDuplicate = new BlockChainDataBaseDefaultImpl(dbPath+"BlockChainDataBaseDuplicate",incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,blockChainDataBaseDuplicate, synchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCore(miner,synchronizer);

        Transaction transaction = new Transaction();
/*        transaction.setTimestamp();
        transaction.setTransactionUUID();*/

        //minerTransactionDtoDataBase.insertTransaction(transaction);

        int height = blockChainDataBase.findTailBlock().getHeight();
        for(int i=1;i<=height;i++){
            Block block = blockChainDataBase.findBlockByBlockHeight(i);
            System.out.println(new Gson().toJson(block));
        }
    }
}
