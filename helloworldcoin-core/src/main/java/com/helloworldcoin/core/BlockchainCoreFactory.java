package com.helloworldcoin.core;

import com.helloworldcoin.core.impl.*;
import com.helloworldcoin.core.tool.ResourceTool;

/**
 * BlockchainCore Factory
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainCoreFactory {

    /**
     * Create BlockchainCore instance
     */
    public static BlockchainCore createDefaultBlockchainCore(){
        return createBlockchainCore(ResourceTool.getDataRootPath());
    }

    /**
     * Create BlockchainCore instance
     *
     * @param corePath BlockchainCore Data storage path
     */
    public static BlockchainCore createBlockchainCore(String corePath) {

        CoreConfiguration coreConfiguration = new CoreConfigurationDefaultImpl(corePath);
        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensusImpl();
        VirtualMachine virtualMachine = new StackBasedVirtualMachine();
        BlockchainDatabase blockchainDatabase = new BlockchainDatabaseDefaultImpl(coreConfiguration,incentive,consensus,virtualMachine);

        UnconfirmedTransactionDatabase unconfirmedTransactionDatabase = new UnconfirmedTransactionDatabaseDefaultImpl(coreConfiguration);
        Wallet wallet = new WalletImpl(coreConfiguration,blockchainDatabase);
        Miner miner = new MinerDefaultImpl(coreConfiguration,wallet,blockchainDatabase,unconfirmedTransactionDatabase);
        return new BlockchainCoreImpl(coreConfiguration,blockchainDatabase,unconfirmedTransactionDatabase,wallet,miner);
    }
}
