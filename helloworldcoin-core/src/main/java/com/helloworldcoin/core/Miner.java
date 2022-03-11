package com.helloworldcoin.core;

/**
 * miner
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class Miner {

    protected CoreConfiguration coreConfiguration;
    protected Wallet wallet;
    protected BlockchainDatabase blockchainDatabase;
    protected UnconfirmedTransactionDatabase unconfirmedTransactionDatabase;


    //region miner related
    /**
     * start miner
     * Miners have two states: active and deactive.
     * If the miner is in active state, the miner will do work , such as mining.
     * If the miner is in deactive, the miner will not do any work.
     */
    public abstract void start() ;

    /**
     * Whether the miner is active status?
     */
    public abstract boolean isActive() ;
    /**
     * active Miner: Set the miner as active status.
     */
    public abstract void active() ;
    /**
     * deactive Miner: Set the miner as deactive status.
     */
    public abstract void deactive() ;

    /**
     * Set the maximum block height that miners can mine
     */
    public abstract void setMinerMineMaxBlockHeight(long maxHeight) ;
    /**
     * Get the maximum block height that miner can mine
     */
    public abstract long getMinerMineMaxBlockHeight( ) ;
    //endregion




    //region get set
    public Wallet getWallet() {
        return wallet;
    }

    public BlockchainDatabase getBlockchainDatabase() {
        return blockchainDatabase;
    }

    public UnconfirmedTransactionDatabase getUnconfirmedTransactionDatabase() {
        return unconfirmedTransactionDatabase;
    }

    public CoreConfiguration getCoreConfiguration() {
        return coreConfiguration;
    }
    //endregion
}