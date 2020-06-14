package com.xingkaichun.helloworldblockchain.netcore.timer;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbranch.BlockchainBranchDto;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainBranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;

/**
 * 区块链分叉处理
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainBranchHandler {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainBranchHandler.class);

    private BlockChainBranchService blockChainBranchService;
    private Gson gson;

    public BlockchainBranchHandler(BlockChainBranchService blockChainBranchService) {
        this.blockChainBranchService = blockChainBranchService;
        this.gson = new Gson();
    }

    public final static String INIT_BLOCKCHAIN_BRANCH_FILE_NAME = "InitBlockchainBranch.txt";

    public void startThread() throws Exception {

        if(!blockChainBranchService.isBlockchainConfirmABranch()){
        //TODO 默认分支
            /*            URL url = Thread.currentThread().getContextClassLoader().getResource(INIT_BLOCKCHAIN_BRANCH_FILE_NAME);
            logger.info(String.format("使用文件%s初始化区块链的分支。 ",url));
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INIT_BLOCKCHAIN_BRANCH_FILE_NAME);
            String context = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
            Type jsonType = new TypeToken<BlockchainBranchDto>() {}.getType();
            BlockchainBranchDto blockchainBranchDto = gson.fromJson(context,jsonType);
            blockChainBranchService.updateBranchchainBranch(blockchainBranchDto.getBlockList());*/
        }

        new Thread(()->{
            while (true){
                try {
                    blockChainBranchService.branchchainBranchHandler();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                try {
                    Thread.sleep(10*60*1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }
}
