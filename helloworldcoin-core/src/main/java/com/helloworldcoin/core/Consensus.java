package com.helloworldcoin.core;

import com.helloworldcoin.core.model.Block;

import java.io.Serializable;

/**
 * Consensus
 *
 * @author x.king xdotking@gmail.com
 */
public abstract class Consensus implements Serializable {

    /**
     * Check whether the block satisfies the consensus
     */
    public abstract boolean checkConsensus(BlockchainDatabase blockchainDatabase, Block block) ;

    /**
     * Calculate mining difficulty of the target block
     */
    public abstract String calculateDifficult(BlockchainDatabase blockchainDatabase, Block block) ;
}

