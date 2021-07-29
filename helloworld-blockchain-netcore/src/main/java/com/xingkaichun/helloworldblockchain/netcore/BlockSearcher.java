package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockDtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.GetBlockRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.GetBlockResponse;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.setting.GenesisBlockSetting;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;

/**
 * 区块搜索器
 * 如果发现区块链网络中有可以进行同步的区块，则尝试同步区块到本地区块链。
 *
 * @author 邢开春 409060350@qq.com
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
                synchronizeBlocks();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getSearchBlockTimeInterval());
            }
        } catch (Exception e) {
            SystemUtil.errorExit("在区块链网络中同步节点的区块出现异常",e);
        }
    }

    /**
     * 搜索新的区块，并同步这些区块到本地区块链系统
     */
    private void synchronizeBlocks() {
        if(!netCoreConfiguration.isAutoSearchBlock()){
            return;
        }
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            if(!netCoreConfiguration.isAutoSearchBlock()){
                return;
            }
            long blockchainHeight = blockchainCore.queryBlockchainHeight();
            //本地区块链高度小于远程节点区块链高度，此时需要将远程节点的区块同步到本地区块链。
            if(blockchainHeight < node.getBlockchainHeight()){
                //复制主区块链核心的区块至从区块链核心
                duplicateBlockchainCore(blockchainCore, slaveBlockchainCore);
                //同步远程节点的区块到本地，未分叉同步至主链，分叉同步至从区块链核心
                synchronizeRemoteNodeBlock(blockchainCore,slaveBlockchainCore,nodeService,node);
                //同步从区块链核心的区块至主区块链核心
                promoteMasterBlockchainCore(blockchainCore, slaveBlockchainCore);
            }
        }
    }

    /**
     * 复制区块链核心的区块，操作完成后，'复制来源区块链核心'区块数据不发生变化，'复制去向区块链核心'的区块数据与主区块链核心的区块数据保持一致。
     * @param fromBlockchainCore 复制来源区块链核心
     * @param toBlockchainCore 复制去向区块链核心
     */
    private void duplicateBlockchainCore(BlockchainCore fromBlockchainCore,BlockchainCore toBlockchainCore) {
        //删除'复制去向区块链核心'区块
        while(true){
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
        //增加'复制去向区块链核心'区块
        while(true){
            long toBlockchainHeight = toBlockchainCore.queryBlockchainHeight();
            Block nextBlock = fromBlockchainCore.queryBlockByBlockHeight(toBlockchainHeight+1) ;
            if(nextBlock == null){
                break;
            }
            toBlockchainCore.addBlock(nextBlock);
        }
    }


    /**
     * 增加主区块链的区块，操作完成后，从区块链核心区块数据不发生变化，主区块链核心高度不变或者增长。
     * @param masterBlockchainCore 主区块链核心
     * @param slaveBlockchainCore 从区块链核心
     */
    private void promoteMasterBlockchainCore(BlockchainCore masterBlockchainCore, BlockchainCore slaveBlockchainCore) {
        //此时，从区块链核心高度低于主区块链核心高度，主区块链核心高度不能增加，结束逻辑。
        if(masterBlockchainCore.queryBlockchainHeight() >= slaveBlockchainCore.queryBlockchainHeight()){
            return;
        }
        //硬分叉
        if(isHardFork(masterBlockchainCore,slaveBlockchainCore)){
            return;
        }
        //此时，从区块链核心高度高于主区块链核心高度，且未硬分叉，可以增加主区块链核心的高度。
        duplicateBlockchainCore(slaveBlockchainCore,masterBlockchainCore);
    }


    /**
     * 同步远程节点的区块到本地，未分叉同步至主链，硬分叉不同步，软分叉同步至从链
     */
    public void synchronizeRemoteNodeBlock(BlockchainCore masterBlockchainCore, BlockchainCore slaveBlockchainCore, NodeService nodeService, Node node) {

        Block masterBlockchainTailBlock = masterBlockchainCore.queryTailBlock();
        long masterBlockchainTailBlockHeight = masterBlockchainCore.queryBlockchainHeight();

        //本地区块链与node区块链是否分叉？
        boolean fork = false;
        if(masterBlockchainTailBlockHeight == GenesisBlockSetting.HEIGHT){
            fork = false;
        } else {
            GetBlockRequest getBlockRequest = new GetBlockRequest();
            getBlockRequest.setBlockHeight(masterBlockchainTailBlockHeight);
            GetBlockResponse getBlockResponse = new NodeClientImpl(node.getIp()).getBlock(getBlockRequest);
            if(getBlockResponse == null){
                return;
            }
            BlockDto blockDto = getBlockResponse.getBlock();
            //没有查询到区块，代表着远程节点的高度没有本地大
            if(blockDto == null){
                return;
            }
            String blockHash = BlockDtoTool.calculateBlockHash(blockDto);
            fork = !StringUtil.isEquals(masterBlockchainTailBlock.getHash(), blockHash);
        }

        if(fork){
            //分叉
            //求分叉区块的高度，此时已知分叉了，从当前高度依次递减1，判断高度相同的区块的是否相等，若相等，(高度+1)即开始分叉高度。
            long forkBlockHeight = masterBlockchainTailBlockHeight;
            while (true) {
                if (forkBlockHeight <= GenesisBlockSetting.HEIGHT) {
                    break;
                }
                GetBlockRequest getBlockRequest = new GetBlockRequest();
                getBlockRequest.setBlockHeight(forkBlockHeight);
                GetBlockResponse getBlockResponse = new NodeClientImpl(node.getIp()).getBlock(getBlockRequest);
                if(getBlockResponse == null){
                    return;
                }
                BlockDto remoteBlock = getBlockResponse.getBlock();
                if(remoteBlock == null){
                    return;
                }
                Block localBlock = slaveBlockchainCore.queryBlockByBlockHeight(forkBlockHeight);
                if(BlockDtoTool.isBlockEquals(Model2DtoTool.block2BlockDto(localBlock),remoteBlock)){
                    break;
                }
                //分叉长度过大，不可同步。这里，已经形成了硬分叉(两条完全不同的区块链)。
                if (masterBlockchainTailBlockHeight-forkBlockHeight+1 >= netCoreConfiguration.getHardForkBlockCount()) {
                    return;
                }
                forkBlockHeight--;
            }
            forkBlockHeight++;
            //从分叉高度开始同步
            slaveBlockchainCore.deleteBlocks(forkBlockHeight);
            while (true){
                GetBlockRequest getBlockRequest = new GetBlockRequest();
                getBlockRequest.setBlockHeight(forkBlockHeight);
                GetBlockResponse getBlockResponse = new NodeClientImpl(node.getIp()).getBlock(getBlockRequest);
                if(getBlockResponse == null){
                    return;
                }
                BlockDto remoteBlock = getBlockResponse.getBlock();
                if(remoteBlock == null){
                    return;
                }
                boolean isAddBlockSuccess = slaveBlockchainCore.addBlockDto(remoteBlock);
                if(!isAddBlockSuccess){
                    return;
                }
                forkBlockHeight++;
            }
        } else {
            //未分叉
            while (true){
                long nextBlockHeight = masterBlockchainCore.queryBlockchainHeight()+1;
                GetBlockRequest getBlockRequest = new GetBlockRequest();
                getBlockRequest.setBlockHeight(nextBlockHeight);
                GetBlockResponse getBlockResponse = new NodeClientImpl(node.getIp()).getBlock(getBlockRequest);
                if(getBlockResponse == null){
                    return;
                }
                BlockDto blockDto = getBlockResponse.getBlock();
                if(blockDto == null){
                    return;
                }
                boolean isAddBlockSuccess = masterBlockchainCore.addBlockDto(blockDto);
                if(!isAddBlockSuccess){
                    return;
                }
            }
        }
    }

    /**
     * 是否硬分叉
     * @param longer 两个形参中区块链高度较长的区块链核心
     * @param shorter 两个形参中区块链高度较长的区块链核心
     */
    private boolean isHardFork(BlockchainCore longer, BlockchainCore shorter) {
        long shorterBlockchainHeight = shorter.queryBlockchainHeight();
        if(shorterBlockchainHeight < netCoreConfiguration.getHardForkBlockCount()){
            return false;
        }

        long criticalPointBlocHeight = shorterBlockchainHeight-netCoreConfiguration.getHardForkBlockCount()+1;
        Block longerBlock = longer.queryBlockByBlockHeight(criticalPointBlocHeight);
        Block shorterBlock = shorter.queryBlockByBlockHeight(criticalPointBlocHeight);
        return !BlockTool.isBlockEquals(longerBlock, shorterBlock);
    }

}
