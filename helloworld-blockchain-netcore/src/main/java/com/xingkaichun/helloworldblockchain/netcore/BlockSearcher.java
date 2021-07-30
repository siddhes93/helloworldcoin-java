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
 * 尝试发现区块链网络中是否有区块链高度比自身区块链高度高的节点，若发现，则尝试同步区块到本地区块链。
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
                //同步远程节点的区块到本地，未分叉同步至主链，分叉同步至从区块链核心
                synchronizeRemoteNodeBlock(blockchainCore,slaveBlockchainCore,nodeService,node);
            }
        }
    }

    /**
     * 复制区块链核心的区块，操作完成后，'来源区块链核心'区块数据不发生变化，'去向区块链核心'的区块数据与'来源区块链核心'的区块数据保持一致。
     * @param fromBlockchainCore 来源区块链核心
     * @param toBlockchainCore 去向区块链核心
     */
    private void duplicateBlockchainCore(BlockchainCore fromBlockchainCore,BlockchainCore toBlockchainCore) {
        //删除'去向区块链核心'区块
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
        //增加'去向区块链核心'区块
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
     * 增加"去向区块链核心"的区块，操作完成后，"来源区块链核心"的区块不发生变化，"去向区块链核心"的高度不变或者增长。
     * @param fromBlockchainCore "来源区块链核心"
     * @param toBlockchainCore "去向区块链核心"
     */
    private void promoteBlockchainCore(BlockchainCore fromBlockchainCore, BlockchainCore toBlockchainCore) {
        //此时，"去向区块链核心高度"大于"来源区块链核心高度"，"去向区块链核心高度"不能增加，结束逻辑。
        if(toBlockchainCore.queryBlockchainHeight() >= fromBlockchainCore.queryBlockchainHeight()){
            return;
        }
        //硬分叉
        if(isHardFork(toBlockchainCore,fromBlockchainCore)){
            return;
        }
        //此时，"去向区块链核心高度"小于"来源区块链核心高度"，且未硬分叉，可以增加"去向区块链核心高度"
        duplicateBlockchainCore(fromBlockchainCore,toBlockchainCore);
    }


    /**
     * 同步远程节点的区块到本地，未分叉同步至主链，硬分叉不同步，软分叉同步至从链
     */
    public void synchronizeRemoteNodeBlock(BlockchainCore masterBlockchainCore, BlockchainCore slaveBlockchainCore, NodeService nodeService, Node node) {

        Block masterBlockchainTailBlock = masterBlockchainCore.queryTailBlock();
        long masterBlockchainHeight = masterBlockchainCore.queryBlockchainHeight();

        //本地区块链与node区块链是否分叉？
        boolean fork = false;
        if(masterBlockchainHeight == GenesisBlockSetting.HEIGHT){
            fork = false;
        } else {
            GetBlockRequest getBlockRequest = new GetBlockRequest();
            getBlockRequest.setBlockHeight(masterBlockchainHeight);
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
            //复制主区块链核心的区块至从区块链核心
            duplicateBlockchainCore(masterBlockchainCore, slaveBlockchainCore);
            //求分叉区块的高度，此时已知分叉了，从当前高度依次递减1，判断高度相同的区块的是否相等，若相等，(高度+1)即开始分叉高度。
            long forkBlockHeight = masterBlockchainHeight;
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
                    break;
                }
                Block localBlock = slaveBlockchainCore.queryBlockByBlockHeight(forkBlockHeight);
                if(BlockDtoTool.isBlockEquals(Model2DtoTool.block2BlockDto(localBlock),remoteBlock)){
                    break;
                }
                //分叉长度过大，不可同步。这里，已经形成了硬分叉(两条完全不同的区块链)。
                if (masterBlockchainHeight-forkBlockHeight+1 >= netCoreConfiguration.getHardForkBlockCount()) {
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
                    break;
                }
                boolean isAddBlockSuccess = slaveBlockchainCore.addBlockDto(remoteBlock);
                if(!isAddBlockSuccess){
                    return;
                }
                forkBlockHeight++;
            }
            //同步从区块链核心的区块至主区块链核心
            promoteBlockchainCore(slaveBlockchainCore, masterBlockchainCore);
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
     */
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

}
