package com.helloworldcoin.setting;

/**
 * incentive setting
 *
 * @author x.king xdotking@gmail.com
 */
public class IncentiveSetting {

    //expected time to mine a block (unit: millisecond)
    public static final long BLOCK_TIME = 1000 * 60 * 10;
    //the number of blocks in a mining difficulty cycle
    public static final long INTERVAL_BLOCK_COUNT = 6 * 24 * 7 * 2;
    //the expected cycle time in a mining cycle (unit: milliseconds)
    public static final long INTERVAL_TIME = BLOCK_TIME * INTERVAL_BLOCK_COUNT;
    //block initialization incentive
    public static final long BLOCK_INIT_INCENTIVE = 50L * 100000000L;
    //incentive amount halving cycle: incentives are halved every 210,000 blocks
    public static final long INCENTIVE_HALVE_INTERVAL = 210000L;
}
