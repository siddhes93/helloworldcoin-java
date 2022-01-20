package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.BlockchainDatabase;
import com.helloworldcoin.core.Incentive;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.BlockTool;
import com.helloworldcoin.setting.IncentiveSetting;
import com.helloworldcoin.util.LogUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class IncentiveDefaultImpl extends Incentive {
    //TODO 封装大数?
    @Override
    public long incentiveValue(BlockchainDatabase blockchainDatabase, Block block) {
        //给予矿工的挖矿津贴
        long minerSubsidy = getMinerSubsidy(block);
        //给予矿工的交易手续费
        long minerFee = BlockTool.getBlockFee(block);
        //总的激励
        return minerSubsidy + minerFee;
    }

    @Override
    public boolean checkIncentive(BlockchainDatabase blockchainDatabase, Block block) {
        long writeIncentiveValue = BlockTool.getWritedIncentiveValue(block);
        long targetIncentiveValue = incentiveValue(blockchainDatabase,block);
        if(writeIncentiveValue != targetIncentiveValue){
            LogUtil.debug("区块数据异常，挖矿奖励数据异常。");
            return false;
        }
        return true;
    }

    private static long getMinerSubsidy(Block block) {
        long initCoin = IncentiveSetting.BLOCK_INIT_INCENTIVE;
        long multiple = (block.getHeight() - 1L) / IncentiveSetting.INCENTIVE_HALVING_INTERVAL;
        while (multiple > 0){
            initCoin = initCoin / 2L;
            --multiple;
        }
        return initCoin;
    }
}