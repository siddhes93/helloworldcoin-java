package com.helloworldcoin.core;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionRequest;
import com.helloworldcoin.core.model.wallet.AutoBuildTransactionResponse;
import com.helloworldcoin.core.model.wallet.Payee;
import com.helloworldcoin.core.tool.ResourceTool;
import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.ThreadUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WalletTest {

    @Test
    public void autoBuildTransactionTest()
    {
        FileUtil.deleteDirectory(ResourceTool.getTestDataRootPath());

        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(ResourceTool.getTestDataRootPath());
        blockchainCore.getMiner().setMinerMineMaxBlockHeight(1L);
        blockchainCore.start();
        blockchainCore.getMiner().active();
        //sleep for mining
        ThreadUtil.millisecondSleep(10000);

        Block block1 = blockchainCore.getBlockchainDatabase().queryTailBlock();
        Assert.assertEquals(1,block1.getHeight());
        Assert.assertNotNull(blockchainCore.queryUnspentTransactionOutputByAddress(block1.getTransactions().get(0).getOutputs().get(0).getAddress()));





        Wallet wallet = blockchainCore.getWallet();

        String payeeAddress = "12F5oN7H2YspbPwUmwvdEq8uhSeKawN9Hh";
        long payeeValue = 5000000000L;

        Payee payee = new Payee();
        payee.setValue(payeeValue);
        payee.setAddress(payeeAddress);
        List<Payee> nonChangePayees = new ArrayList<>();
        nonChangePayees.add(payee);
        AutoBuildTransactionRequest request = new AutoBuildTransactionRequest();
        request.setNonChangePayees(nonChangePayees);

        AutoBuildTransactionResponse response = wallet.autoBuildTransaction(request);

        blockchainCore.getMiner().getUnconfirmedTransactionDatabase().insertTransaction(response.getTransaction());
        blockchainCore.getMiner().setMinerMineMaxBlockHeight(2L);

        //sleep for mining
        ThreadUtil.millisecondSleep(10000);


        Block block2 = blockchainCore.getBlockchainDatabase().queryTailBlock();
        Assert.assertEquals(2,block2.getHeight());
        Assert.assertEquals(payeeAddress,block2.getTransactions().get(1).getOutputs().get(0).getAddress());
        Assert.assertEquals(payeeValue,block2.getTransactions().get(1).getOutputs().get(0).getValue());
        Assert.assertNotNull(blockchainCore.queryUnspentTransactionOutputByAddress(payeeAddress));
        Assert.assertNull(blockchainCore.queryUnspentTransactionOutputByAddress(block1.getTransactions().get(0).getOutputs().get(0).getAddress()));
    }
}
