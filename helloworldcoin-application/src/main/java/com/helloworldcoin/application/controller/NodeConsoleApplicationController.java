package com.helloworldcoin.application.controller;

import com.helloworldcoin.application.vo.NodeConsoleApplicationApi;
import com.helloworldcoin.application.vo.block.DeleteBlocksRequest;
import com.helloworldcoin.application.vo.block.DeleteBlocksResponse;
import com.helloworldcoin.application.vo.framwork.Response;
import com.helloworldcoin.application.vo.miner.*;
import com.helloworldcoin.application.vo.node.*;
import com.helloworldcoin.application.vo.synchronizer.*;
import com.helloworldcoin.netcore.BlockchainNetCore;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Node Console Application Controller : active miner、deactive miner and so on.
 *
 * @author x.king xdotking@gmail.com
 */
@RestController
public class NodeConsoleApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;



    /**
     * is miner active
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<IsMinerActiveResponse> isMinerActive(@RequestBody IsMinerActiveRequest request){
        try {
            boolean isMineActive = blockchainNetCore.getBlockchainCore().getMiner().isActive();
            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMinerInActiveState(isMineActive);
            return Response.success(response);
        } catch (Exception e){
            String message = "'is miner active' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * active miner
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public Response<ActiveMinerResponse> activeMiner(@RequestBody ActiveMinerRequest request){
        try {
            blockchainNetCore.getBlockchainCore().getMiner().active();
            ActiveMinerResponse response = new ActiveMinerResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'active miner' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * deactive miner
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeactiveMinerResponse> deactiveMiner(@RequestBody DeactiveMinerRequest request){
        try {
            blockchainNetCore.getBlockchainCore().getMiner().deactive();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'deactive miner' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * is auto search block
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<IsAutoSearchBlockResponse> isAutoSearchBlock(@RequestBody IsAutoSearchBlockRequest request){
        try {
            boolean isAutoSearchBlock = blockchainNetCore.getNetCoreConfiguration().isAutoSearchBlock();
            IsAutoSearchBlockResponse response = new IsAutoSearchBlockResponse();
            response.setAutoSearchBlock(isAutoSearchBlock);
            return Response.success(response);
        } catch (Exception e){
            String message = "'is auto search block' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * active auto search block
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<ActiveAutoSearchBlockResponse> activeAutoSearchBlock(@RequestBody ActiveAutoSearchBlockRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().activeAutoSearchBlock();
            ActiveAutoSearchBlockResponse response = new ActiveAutoSearchBlockResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'active auto search block' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * deactive auto search block
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeactiveAutoSearchBlockResponse> deactiveAutoSearchBlock(@RequestBody DeactiveAutoSearchBlockRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().deactiveAutoSearchBlock();
            DeactiveAutoSearchBlockResponse response = new DeactiveAutoSearchBlockResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'deactive auto search block' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * add node
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            String ip = request.getIp();
            if(blockchainNetCore.getNodeService().queryNode(ip) != null){
                AddNodeResponse response = new AddNodeResponse();
                response.setAddNodeSuccess(true);
                return Response.success(response);
            }
            Node node = new Node();
            node.setIp(ip);
            node.setBlockchainHeight(0);
            blockchainNetCore.getNodeService().addNode(node);
            AddNodeResponse response = new AddNodeResponse();
            response.setAddNodeSuccess(true);
            return Response.success(response);
        } catch (Exception e){
            String message = "'add node' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * update node
     */
    @RequestMapping(value = NodeConsoleApplicationApi.UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<UpdateNodeResponse> updateNode(@RequestBody UpdateNodeRequest request){
        try {
            Node node = new Node();
            node.setIp(request.getIp());
            node.setBlockchainHeight(request.getBlockchainHeight());
            blockchainNetCore.getNodeService().updateNode(node);
            UpdateNodeResponse response = new UpdateNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'update node' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * delete node
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DELETE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeleteNodeResponse> deleteNode(@RequestBody DeleteNodeRequest request){
        try {
            blockchainNetCore.getNodeService().deleteNode(request.getIp());
            DeleteNodeResponse response = new DeleteNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'delete node' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * query all nodes
     */
    @RequestMapping(value = NodeConsoleApplicationApi.QUERY_ALL_NODES,method={RequestMethod.GET,RequestMethod.POST})
    public Response<QueryAllNodesResponse> queryAllNodes(@RequestBody QueryAllNodesRequest request){
        try {
            List<Node> nodes = blockchainNetCore.getNodeService().queryAllNodes();

            List<NodeVo> nodeVos = new ArrayList<>();
            if(nodes != null){
                for (Node node:nodes) {
                    NodeVo nodeVo = new NodeVo();
                    nodeVo.setIp(node.getIp());
                    nodeVo.setBlockchainHeight(node.getBlockchainHeight());
                    nodeVos.add(nodeVo);
                }
            }

            QueryAllNodesResponse response = new QueryAllNodesResponse();
            response.setNodes(nodeVos);
            return Response.success(response);
        } catch (Exception e){
            String message = "'query all nodes' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * is auto search node
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<IsAutoSearchNodeResponse> isAutoSearchNode(@RequestBody IsAutoSearchNodeRequest request){
        try {
            boolean isAutoSearchNode = blockchainNetCore.getNetCoreConfiguration().isAutoSearchNode();
            IsAutoSearchNodeResponse response = new IsAutoSearchNodeResponse();
            response.setAutoSearchNode(isAutoSearchNode);
            return Response.success(response);
        } catch (Exception e){
            String message = "'is auto search node' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * active auto search node
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<ActiveAutoSearchNodeResponse> activeAutoSearchNode(@RequestBody ActiveAutoSearchNodeRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().activeAutoSearchNode();
            ActiveAutoSearchNodeResponse response = new ActiveAutoSearchNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'active auto search node' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * deactive auto search node
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeactiveAutoSearchNodeResponse> deactiveAutoSearchNode(@RequestBody DeactiveAutoSearchNodeRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().deactiveAutoSearchNode();
            DeactiveAutoSearchNodeResponse response = new DeactiveAutoSearchNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'deactive auto search node' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * delete blocks
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DELETE_BLOCKS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeleteBlocksResponse> deleteBlocks(@RequestBody DeleteBlocksRequest request){
        try {
            blockchainNetCore.getBlockchainCore().deleteBlocks(request.getBlockHeight());
            DeleteBlocksResponse response = new DeleteBlocksResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'delete blocks' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * Get Miner Mine Max Block Height
     */
    @RequestMapping(value = NodeConsoleApplicationApi.GET_MINER_MINE_MAX_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<GetMinerMineMaxBlockHeightResponse> getMinerMineMaxBlockHeight(@RequestBody GetMinerMineMaxBlockHeightRequest request){
        try {
            long  maxBlockHeight = blockchainNetCore.getBlockchainCore().getMiner().getMinerMineMaxBlockHeight();
            GetMinerMineMaxBlockHeightResponse response = new GetMinerMineMaxBlockHeightResponse();
            response.setMaxBlockHeight(maxBlockHeight);
            return Response.success(response);
        } catch (Exception e){
            String message = "'Get Miner Mine Max Block Height' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * Set Miner Mine Max Block Height
     */
    @RequestMapping(value = NodeConsoleApplicationApi.SET_MINER_MINE_MAX_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<SetMinerMineMaxBlockHeightResponse> setMinerMineMaxBlockHeight(@RequestBody SetMinerMineMaxBlockHeightRequest request){
        try {
            long height = request.getMaxBlockHeight();
            blockchainNetCore.getBlockchainCore().getMiner().setMinerMineMaxBlockHeight(height);
            SetMinerMineMaxBlockHeightResponse response = new SetMinerMineMaxBlockHeightResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "'Set Miner Mine Max Block Height' error.";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
}