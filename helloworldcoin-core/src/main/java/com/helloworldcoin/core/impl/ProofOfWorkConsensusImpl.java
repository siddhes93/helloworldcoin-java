package com.helloworldcoin.core.impl;

import com.helloworldcoin.core.BlockchainDatabase;
import com.helloworldcoin.core.Consensus;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.BlockTool;
import com.helloworldcoin.setting.GenesisBlockSetting;
import com.helloworldcoin.setting.IncentiveSetting;
import com.helloworldcoin.util.StringUtil;

import java.math.BigInteger;

/**
 * Proof Of Work Consensus
 *
 * @author x.king xdotking@gmail.com
 */
public class ProofOfWorkConsensusImpl extends Consensus {

    @Override
    public boolean checkConsensus(BlockchainDatabase blockchainDatabase, Block block) {
        String difficulty = block.getDifficulty();
        if(StringUtil.isEmpty(difficulty)){
            difficulty = calculateDifficult(blockchainDatabase,block);
        }

        String hash = block.getHash();
        if(StringUtil.isEmpty(hash)){
            hash = BlockTool.calculateBlockHash(block);
        }
        return new BigInteger(difficulty,16).compareTo(new BigInteger(hash,16)) > 0;
    }

    public String calculateDifficult(BlockchainDatabase blockchainDatabase, Block targetBlock) {

        String targetDifficult;
        long targetBlockHeight = targetBlock.getHeight();
        if(targetBlockHeight <= IncentiveSetting.INTERVAL_BLOCK_COUNT * 2){
            targetDifficult = GenesisBlockSetting.DIFFICULTY;
            return targetDifficult;
        }

        Block targetBlockPreviousBlock = blockchainDatabase.queryBlockByBlockHeight(targetBlockHeight-1);
        if (targetBlockPreviousBlock.getHeight() % IncentiveSetting.INTERVAL_BLOCK_COUNT != 0){
            targetDifficult = targetBlockPreviousBlock.getDifficulty();
            return targetDifficult;
        }

        Block previousIntervalLastBlock = targetBlockPreviousBlock;
        Block previousPreviousIntervalLastBlock = blockchainDatabase.queryBlockByBlockHeight(previousIntervalLastBlock.getHeight()- IncentiveSetting.INTERVAL_BLOCK_COUNT);
        long previousIntervalActualTimespan = previousIntervalLastBlock.getTimestamp() - previousPreviousIntervalLastBlock.getTimestamp();

        BigInteger bigIntegerTargetDifficult =
                new BigInteger(previousIntervalLastBlock.getDifficulty(),16)
                        .multiply(new BigInteger(String.valueOf(previousIntervalActualTimespan), 10))
                        .divide(new BigInteger(String.valueOf(IncentiveSetting.INTERVAL_TIME), 10));
        return bigIntegerTargetDifficult.toString(16);
    }
}
