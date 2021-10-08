package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.AutoBuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Payee;
import com.xingkaichun.helloworldblockchain.core.tool.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WalletTest {


    /**
     * 测试是否可以走通支付流程
     */
    @Test
    public void autoBuildTransactionTest()
    {
        FileUtil.deleteDirectory(ResourcePathTool.getTestDataRootPath());

        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(ResourcePathTool.getTestDataRootPath());
        blockchainCore.getMiner().setMinerMineMaxBlockHeight(1L);
        blockchainCore.start();
        blockchainCore.getMiner().active();
        //留5秒时间挖矿
        ThreadUtil.millisecondSleep(10000);

        //测试是否挖出一个区块
        Block block1 = blockchainCore.getBlockchainDatabase().queryTailBlock();
        Assert.assertEquals(1,block1.getHeight());
        Assert.assertNotNull(blockchainCore.queryUnspentTransactionOutputByAddress(block1.getTransactions().get(0).getOutputs().get(0).getAddress()));





        Wallet wallet = blockchainCore.getWallet();

        //收款地址
        String payeeAddress = "12F5oN7H2YspbPwUmwvdEq8uhSeKawN9Hh";
        //收款金额
        long payeeValue = 5000000000L;

        //构造收款方
        Payee payee = new Payee();
        payee.setValue(payeeValue);
        payee.setAddress(payeeAddress);
        List<Payee> nonChangePayees = new ArrayList<>();
        nonChangePayees.add(payee);
        AutoBuildTransactionRequest request = new AutoBuildTransactionRequest();
        request.setNonChangePayees(nonChangePayees);

        //构造交易
        AutoBuildTransactionResponse response = wallet.autoBuildTransaction(request);

        //将交易放入未确认交易池
        blockchainCore.getMiner().getUnconfirmedTransactionDatabase().insertTransaction(response.getTransaction());
        blockchainCore.getMiner().setMinerMineMaxBlockHeight(2L);

        //留5秒时间挖矿
        ThreadUtil.millisecondSleep(10000);


        Block block2 = blockchainCore.getBlockchainDatabase().queryTailBlock();
        //测试是否挖出一个区块
        Assert.assertEquals(2,block2.getHeight());
        //测试挖出的区块第二笔交易的交易输出是否是我们指定的收款地址
        Assert.assertEquals(payeeAddress,block2.getTransactions().get(1).getOutputs().get(0).getAddress());
        //测试挖出的区块第二笔交易的交易输出是否是我们指定的收款金额
        Assert.assertEquals(payeeValue,block2.getTransactions().get(1).getOutputs().get(0).getValue());
        Assert.assertNotNull(blockchainCore.queryUnspentTransactionOutputByAddress(payeeAddress));
        Assert.assertNull(blockchainCore.queryUnspentTransactionOutputByAddress(block1.getTransactions().get(0).getOutputs().get(0).getAddress()));
    }
}
