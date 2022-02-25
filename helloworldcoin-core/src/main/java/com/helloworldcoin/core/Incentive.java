package com.helloworldcoin.core;

import com.helloworldcoin.core.model.Block;

/**
 * Incentive
 * How much digital currency should the system reward miners? It will be determined here.
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class Incentive {

    /**
     * Determine how much digital currency to reward miner
     */
    public abstract long incentiveValue(BlockchainDatabase blockchainDatabase, Block block) ;

    /**
     * Check Incentive
     */
    public abstract boolean checkIncentive(BlockchainDatabase blockchainDatabase, Block block) ;
}
