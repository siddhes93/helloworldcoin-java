package com.helloworldcoin.netcore;

import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.BlockDtoTool;
import com.helloworldcoin.core.tool.BlockTool;
import com.helloworldcoin.core.tool.Model2DtoTool;
import com.helloworldcoin.netcore.client.NodeClient;
import com.helloworldcoin.netcore.client.NodeClientImpl;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.GetBlockRequest;
import com.helloworldcoin.netcore.dto.GetBlockResponse;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.setting.GenesisBlockSetting;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.StringUtil;
import com.helloworldcoin.util.ThreadUtil;

import java.util.List;

/**
 * block searcher : search for blocks in the blockchain network.
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockSearcher {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;
    private BlockchainCore slaveBlockchainCore;


    public BlockSearcher(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore
            , BlockchainCore slaveBlockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
        this.slaveBlockchainCore = slaveBlockchainCore;
    }

    public void start() {
        try {
            while (true){
                searchNodesBlocks();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("'search for blocks in the blockchain network' error.",e);
        }
    }

    /**
     * Search for new blocks and sync these blocks to the local blockchain system
     */
    private void searchNodesBlocks() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            searchNodeBlocks(blockchainCore,slaveBlockchainCore,node);
        }
    }

    /**
     * search blocks
     */
    public void searchNodeBlocks(BlockchainCore masterBlockchainCore, BlockchainCore slaveBlockchainCore, Node node) {
        if(!netCoreConfiguration.isAutoSearchBlock()){
            return;
        }
        long masterBlockchainHeight = masterBlockchainCore.queryBlockchainHeight();
        if(masterBlockchainHeight >= node.getBlockchainHeight()){
            return;
        }
        boolean fork = isForkNode(masterBlockchainCore,node);
        if(fork){
            boolean isHardFork = isHardForkNode(masterBlockchainCore,node);
            if(!isHardFork){
                long forkBlockHeight = getForkBlockHeight(masterBlockchainCore,node);
                duplicateBlockchainCore(masterBlockchainCore, slaveBlockchainCore);
                slaveBlockchainCore.deleteBlocks(forkBlockHeight);
                synchronizeBlocks(slaveBlockchainCore,node,forkBlockHeight);
                promoteBlockchainCore(slaveBlockchainCore, masterBlockchainCore);
            }
        } else {
            long nextBlockHeight = masterBlockchainCore.queryBlockchainHeight()+1;
            synchronizeBlocks(masterBlockchainCore,node,nextBlockHeight);
        }
    }

    /**
     * duplicate BlockchainCore
     */
    private void duplicateBlockchainCore(BlockchainCore fromBlockchainCore,BlockchainCore toBlockchainCore) {
        //delete blocks
        while (true){
            Block toBlockchainTailBlock = toBlockchainCore.queryTailBlock() ;
            if(toBlockchainTailBlock == null){
                break;
            }
            Block fromBlockchainBlock = fromBlockchainCore.queryBlockByBlockHeight(toBlockchainTailBlock.getHeight());
            if(BlockTool.isBlockEquals(fromBlockchainBlock,toBlockchainTailBlock)){
                break;
            }
            toBlockchainCore.deleteTailBlock();
        }
        //add blocks
        while (true){
            long toBlockchainHeight = toBlockchainCore.queryBlockchainHeight();
            Block nextBlock = fromBlockchainCore.queryBlockByBlockHeight(toBlockchainHeight+1) ;
            if(nextBlock == null){
                break;
            }
            toBlockchainCore.addBlock(nextBlock);
        }
    }

    /**
     * promote BlockchainCore
     */
    private void promoteBlockchainCore(BlockchainCore fromBlockchainCore, BlockchainCore toBlockchainCore) {
        if(toBlockchainCore.queryBlockchainHeight() >= fromBlockchainCore.queryBlockchainHeight()){
            return;
        }
        //hard fork
        if(isHardFork(toBlockchainCore,fromBlockchainCore)){
            return;
        }
        duplicateBlockchainCore(fromBlockchainCore,toBlockchainCore);
    }

    private long getForkBlockHeight(BlockchainCore blockchainCore, Node node) {
        long masterBlockchainHeight = blockchainCore.queryBlockchainHeight();
        long forkBlockHeight = masterBlockchainHeight;
        while (true) {
            if (forkBlockHeight <= GenesisBlockSetting.HEIGHT) {
                break;
            }
            GetBlockRequest getBlockRequest = new GetBlockRequest();
            getBlockRequest.setBlockHeight(forkBlockHeight);
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest);
            if(getBlockResponse == null){
                break;
            }
            BlockDto remoteBlock = getBlockResponse.getBlock();
            if(remoteBlock == null){
                break;
            }
            Block localBlock = blockchainCore.queryBlockByBlockHeight(forkBlockHeight);
            if(BlockDtoTool.isBlockEquals(Model2DtoTool.block2BlockDto(localBlock),remoteBlock)){
                break;
            }
            forkBlockHeight--;
        }
        forkBlockHeight++;
        return forkBlockHeight;
    }

    private void synchronizeBlocks(BlockchainCore blockchainCore, Node node, long startBlockHeight) {
        while (true){
            GetBlockRequest getBlockRequest = new GetBlockRequest();
            getBlockRequest.setBlockHeight(startBlockHeight);
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest);
            if(getBlockResponse == null){
                break;
            }
            BlockDto remoteBlock = getBlockResponse.getBlock();
            if(remoteBlock == null){
                break;
            }
            boolean isAddBlockSuccess = blockchainCore.addBlockDto(remoteBlock);
            if(!isAddBlockSuccess){
                break;
            }
            startBlockHeight++;
        }
    }

    private boolean isForkNode(BlockchainCore blockchainCore, Node node) {
        Block block = blockchainCore.queryTailBlock();
        if(block == null){
            return false;
        }
        GetBlockRequest getBlockRequest = new GetBlockRequest();
        getBlockRequest.setBlockHeight(block.getHeight());
        NodeClient nodeClient = new NodeClientImpl(node.getIp());
        GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest);
        //no block with this height exist, so no fork.
        if(getBlockResponse == null){
            return false;
        }
        BlockDto blockDto = getBlockResponse.getBlock();
        if(blockDto == null){
            return false;
        }
        String blockHash = BlockDtoTool.calculateBlockHash(blockDto);
        return !StringUtil.equals(block.getHash(), blockHash);
    }

    private boolean isHardFork(BlockchainCore blockchainCore1, BlockchainCore blockchainCore2) {
        BlockchainCore longer;
        BlockchainCore shorter;
        if(blockchainCore1.queryBlockchainHeight()>=blockchainCore2.queryBlockchainHeight()){
            longer = blockchainCore1;
            shorter = blockchainCore2;
        }else {
            longer = blockchainCore2;
            shorter = blockchainCore1;
        }

        long shorterBlockchainHeight = shorter.queryBlockchainHeight();
        if(shorterBlockchainHeight < netCoreConfiguration.getHardForkBlockCount()){
            return false;
        }

        long criticalPointBlocHeight = shorterBlockchainHeight-netCoreConfiguration.getHardForkBlockCount()+1;
        Block longerBlock = longer.queryBlockByBlockHeight(criticalPointBlocHeight);
        Block shorterBlock = shorter.queryBlockByBlockHeight(criticalPointBlocHeight);
        return !BlockTool.isBlockEquals(longerBlock, shorterBlock);
    }

    private boolean isHardForkNode(BlockchainCore blockchainCore, Node node) {
        long blockchainHeight = blockchainCore.queryBlockchainHeight();
        if (blockchainHeight < netCoreConfiguration.getHardForkBlockCount()) {
            return false;
        }
        long criticalPointBlocHeight = blockchainHeight-netCoreConfiguration.getHardForkBlockCount()+1;
        if(criticalPointBlocHeight <= GenesisBlockSetting.HEIGHT){
            return false;
        }
        GetBlockRequest getBlockRequest = new GetBlockRequest();
        getBlockRequest.setBlockHeight(criticalPointBlocHeight);
        NodeClient nodeClient = new NodeClientImpl(node.getIp());
        GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest);
        if(getBlockResponse == null){
            return false;
        }
        BlockDto remoteBlock = getBlockResponse.getBlock();
        if(remoteBlock == null){
            return false;
        }
        Block localBlock = blockchainCore.queryBlockByBlockHeight(criticalPointBlocHeight);
        return !BlockDtoTool.isBlockEquals(Model2DtoTool.block2BlockDto(localBlock),remoteBlock);
    }
}
