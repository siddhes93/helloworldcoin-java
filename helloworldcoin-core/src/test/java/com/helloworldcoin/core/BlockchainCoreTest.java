package com.helloworldcoin.core;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.ResourceTool;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.JsonUtil;
import com.helloworldcoin.util.SystemUtil;
import org.junit.Assert;
import org.junit.Test;

public class BlockchainCoreTest {

    @Test
    public void blockchainDataFormatTest()
    {
        FileUtil.deleteDirectory(ResourceTool.getTestDataRootPath());

        String stringBlock1 = FileUtil.read(SystemUtil.systemRootDirectory()+"\\helloworldcoin-core"+"\\src\\test\\resources\\blocks\\block1.json");
        BlockDto blockDto1 = JsonUtil.toObject(stringBlock1, BlockDto.class);
        String stringBlock2 = FileUtil.read(SystemUtil.systemRootDirectory()+"\\helloworldcoin-core"+"\\src\\test\\resources\\blocks\\block2.json");
        BlockDto blockDto2 = JsonUtil.toObject(stringBlock2, BlockDto.class);
        String stringBlock3 = FileUtil.read(SystemUtil.systemRootDirectory()+"\\helloworldcoin-core"+"\\src\\test\\resources\\blocks\\block3.json");
        BlockDto blockDto3 = JsonUtil.toObject(stringBlock3, BlockDto.class);


        String block1Hash = "e213eaae8259e1aca2044f35036ec5fc3c4370a33fa28353a749e8257e1d2e9e";
        String block2Hash = "8759b498f57e3b359759b7723850a99968f6e8b4bd8143e2ea41b3dbfbb59942";
        String block3Hash = "739f3554dae0a4d2b73142ae8be398fccc8971c9fac52baea1741f4205dc0315";

        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(ResourceTool.getTestDataRootPath());
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
