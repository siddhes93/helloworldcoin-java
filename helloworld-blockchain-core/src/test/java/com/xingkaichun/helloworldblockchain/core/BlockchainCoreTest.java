package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tool.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import org.junit.Assert;
import org.junit.Test;

public class BlockchainCoreTest {

    /**
     * 测试数据格式是否发生改变，若改变，提供的几个区块数据99%的可能性不能被加入区块链。
     * 数据格式包含: 交易哈希生产规则、区块哈希生成规则、默克尔树根计算规则、区块链链式结构、虚拟机执行规则等等
     * 为什么验证几个区块的数据，就可以验证那么多的细节？
     * 因为区块链是一个完整的系统，牵一发而动全身，任一环节有所改变，将影响后续链路。
     */
    @Test
    public void blockchainDataFormatTest()
    {
        FileUtil.deleteDirectory(ResourcePathTool.getTestDataRootPath());

        String stringBlock1 = FileUtil.read(SystemUtil.systemRootDirectory()+"\\helloworld-blockchain-core"+"\\src\\test\\resources\\blocks\\block1.json");
        BlockDto blockDto1 = JsonUtil.toObject(stringBlock1, BlockDto.class);
        String stringBlock2 = FileUtil.read(SystemUtil.systemRootDirectory()+"\\helloworld-blockchain-core"+"\\src\\test\\resources\\blocks\\block2.json");
        BlockDto blockDto2 = JsonUtil.toObject(stringBlock2, BlockDto.class);
        String stringBlock3 = FileUtil.read(SystemUtil.systemRootDirectory()+"\\helloworld-blockchain-core"+"\\src\\test\\resources\\blocks\\block3.json");
        BlockDto blockDto3 = JsonUtil.toObject(stringBlock3, BlockDto.class);


        String block1Hash = "e213eaae8259e1aca2044f35036ec5fc3c4370a33fa28353a749e8257e1d2e9e";
        String block2Hash = "8759b498f57e3b359759b7723850a99968f6e8b4bd8143e2ea41b3dbfbb59942";
        String block3Hash = "739f3554dae0a4d2b73142ae8be398fccc8971c9fac52baea1741f4205dc0315";

        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(ResourcePathTool.getTestDataRootPath());
        blockchainCore.addBlockDto(blockDto1);
        blockchainCore.addBlockDto(blockDto2);
        blockchainCore.addBlockDto(blockDto3);

        Block block1 = blockchainCore.queryBlockByBlockHeight(1);
        Assert.assertEquals(block1Hash,block1.getHash());
        Block block2 = blockchainCore.queryBlockByBlockHeight(2);
        Assert.assertEquals(block2Hash,block2.getHash());
        Block block3 = blockchainCore.queryBlockByBlockHeight(3);
        Assert.assertEquals(block3Hash,block3.getHash());
    }
}
