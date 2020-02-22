package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

import java.io.File;

public class BlockChainCoreFactory {


    public BlockChainCore createBlockChainCore(String blockchainPath,String minerPublicKeyString) throws Exception {

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl(new File(blockchainPath,"BlockChainDataBase").getAbsolutePath(),incentive,consensus);

        TransactionDataBase transactionDataBase = new TransactionDataBaseDefaultImpl();

        MinerTransactionDtoDataBase minerTransactionDtoDataBase = new MinerTransactionDtoDtoDataBaseDefaultImpl(new File(blockchainPath,"MinerTransactionDtoDataBase").getAbsolutePath(),transactionDataBase);
        PublicKeyString minerPublicKey = new PublicKeyString(minerPublicKeyString);
        Miner miner = new MinerDefaultImpl(blockChainDataBase, minerTransactionDtoDataBase,minerPublicKey);

        SynchronizerDataBase synchronizerDataBase = new SynchronizerDataBaseDefaultImpl(new File(blockchainPath,"Synchronizer").getAbsolutePath(),"otherNodeBlock.data",transactionDataBase);
        BlockChainDataBase temporaryBlockChainDataBase = new BlockChainDataBaseDefaultImpl(new File(blockchainPath,"BlockChainDataBaseDuplicate").getAbsolutePath(),incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,temporaryBlockChainDataBase, synchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCoreImpl();
        blockChainCore.setMiner(miner);
        blockChainCore.setSynchronizer(synchronizer);
        blockChainCore.setBlockChainDataBase(blockChainDataBase);
        return blockChainCore;
    }
}
