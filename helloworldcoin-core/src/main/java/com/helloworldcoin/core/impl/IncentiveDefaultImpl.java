package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.BlockchainDatabase;
import com.helloworldcoin.core.Incentive;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.BlockTool;
import com.helloworldcoin.setting.IncentiveSetting;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class IncentiveDefaultImpl extends Incentive {

    @Override
    public long incentiveValue(BlockchainDatabase blockchainDatabase, Block block) {
        long minerSubsidy = getMinerSubsidy(block);
        long minerFee = BlockTool.getBlockFee(block);
        return minerSubsidy + minerFee;
    }

    @Override
    public boolean checkIncentive(BlockchainDatabase blockchainDatabase, Block block) {
        long writeIncentiveValue = BlockTool.getWritedIncentiveValue(block);
        long targetIncentiveValue = incentiveValue(blockchainDatabase,block);
        if(writeIncentiveValue != targetIncentiveValue){
            return false;
        }
        return true;
    }

    private static long getMinerSubsidy(Block block) {
        long initCoin = IncentiveSetting.BLOCK_INIT_INCENTIVE;
        long multiple = (block.getHeight() - 1L) / IncentiveSetting.INCENTIVE_HALVE_INTERVAL;
        while (multiple > 0){
            initCoin = initCoin / 2L;
            --multiple;
        }
        return initCoin;
    }
}