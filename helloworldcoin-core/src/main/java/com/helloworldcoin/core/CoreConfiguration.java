package com.helloworldcoin.core;

/**
 * Core Configuration
 * This class manages the configuration of BlockchainCore.
 * Here you can persist configuration information.
 * In theory, any configuration required by the BlockchainCore module can be obtained from this object.
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class CoreConfiguration {

    //BlockchainCore data storage path
    protected String corePath;

    /**
     * get BlockchainCore data storage path
     */
    public abstract String getCorePath();
    /**
     * Is the miner active?
     */
    public abstract boolean isMinerActive();
    /**
     * active Miner
     */
    public abstract void activeMiner() ;
    /**
     * deactive Miner
     */
    public abstract void deactiveMiner() ;
    /**
     * Set the maximum block height that miners can mine
     */
    public abstract void setMinerMineMaxBlockHeight(long maxHeight) ;

    /**
     * Get the maximum block height that miner can mine
     */
    public abstract long getMinerMineMaxBlockHeight() ;

    /**
     * Miner mining time period
     */
    public abstract long getMinerMineTimeInterval();
}
