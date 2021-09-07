package com.xingkaichun.helloworldblockchain.application.controller;

import com.xingkaichun.helloworldblockchain.application.vo.NodeConsoleApplicationApi;
import com.xingkaichun.helloworldblockchain.application.vo.block.DeleteBlocksRequest;
import com.xingkaichun.helloworldblockchain.application.vo.block.DeleteBlocksResponse;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.Response;
import com.xingkaichun.helloworldblockchain.application.vo.miner.*;
import com.xingkaichun.helloworldblockchain.application.vo.node.*;
import com.xingkaichun.helloworldblockchain.application.vo.synchronizer.*;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点控制台应用控制器：用于控制本地区块链节点，如激活矿工、停用矿工、同步其它节点数据等。
 * 这里的操作都应该需要权限才可以操作，不适合对所有人开放。
 *
 * @author 邢开春 409060350@qq.com
 */
@RestController
public class NodeConsoleApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;



    /**
     * 矿工是否激活
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<IsMinerActiveResponse> isMineActive(@RequestBody IsMinerActiveRequest request){
        try {
            boolean isMineActive = blockchainNetCore.getBlockchainCore().getMiner().isActive();
            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMinerInActiveState(isMineActive);
            return Response.success(response);
        } catch (Exception e){
            String message = "查询矿工是否处于激活状态失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 激活矿工
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public Response<ActiveMinerResponse> activeMiner(@RequestBody ActiveMinerRequest request){
        try {
            blockchainNetCore.getBlockchainCore().getMiner().active();
            ActiveMinerResponse response = new ActiveMinerResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "激活矿工失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 停用矿工
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeactiveMinerResponse> deactiveMiner(@RequestBody DeactiveMinerRequest request){
        try {
            blockchainNetCore.getBlockchainCore().getMiner().deactive();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "停用矿工失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * 是否"自动搜索新区块"
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<IsAutoSearchBlockResponse> isAutoSearchBlock(@RequestBody IsAutoSearchBlockRequest request){
        try {
            boolean isAutoSearchBlock = blockchainNetCore.getNetCoreConfiguration().isAutoSearchBlock();
            IsAutoSearchBlockResponse response = new IsAutoSearchBlockResponse();
            response.setAutoSearchBlock(isAutoSearchBlock);
            return Response.success(response);
        } catch (Exception e){
            String message = "查询[是否自动搜索新区块]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 开启"自动搜索新区块"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<ActiveAutoSearchBlockResponse> activeAutoSearchBlock(@RequestBody ActiveAutoSearchBlockRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().activeAutoSearchBlock();
            ActiveAutoSearchBlockResponse response = new ActiveAutoSearchBlockResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "开启自动搜索新区块选项失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 关闭"自动搜索新区块"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeactiveAutoSearchBlockResponse> deactiveAutoSearchBlock(@RequestBody DeactiveAutoSearchBlockRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().deactiveAutoSearchBlock();
            DeactiveAutoSearchBlockResponse response = new DeactiveAutoSearchBlockResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "关闭自动搜索新区块选项失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * 新增节点
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            String ip = request.getIp();
            if(StringUtil.isNullOrEmpty(ip)){
                return Response.requestParamIllegal();
            }
            if(blockchainNetCore.getNodeService().queryNode(ip) != null){
                //节点存在，认为是成功添加。
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
            String message = "新增节点失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 更新节点信息
     */
    @RequestMapping(value = NodeConsoleApplicationApi.UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<UpdateNodeResponse> updateNode(@RequestBody UpdateNodeRequest request){
        try {
            String ip = request.getIp();
            if(StringUtil.isNullOrEmpty(ip)){
                return Response.requestParamIllegal();
            }
            Node node = new Node();
            node.setIp(ip);
            node.setBlockchainHeight(request.getBlockchainHeight());
            blockchainNetCore.getNodeService().updateNode(node);
            UpdateNodeResponse response = new UpdateNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "更新节点信息失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 删除节点
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DELETE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeleteNodeResponse> deleteNode(@RequestBody DeleteNodeRequest request){
        try {
            blockchainNetCore.getNodeService().deleteNode(request.getIp());
            DeleteNodeResponse response = new DeleteNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "删除节点失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 查询所有节点
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
            String message = "查询节点失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * 是否开启了自动寻找区块链节点的功能
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<IsAutoSearchNodeResponse> isAutoSearchNode(@RequestBody IsAutoSearchNodeRequest request){
        try {
            boolean isAutoSearchNode = blockchainNetCore.getNetCoreConfiguration().isAutoSearchNode();
            IsAutoSearchNodeResponse response = new IsAutoSearchNodeResponse();
            response.setAutoSearchNode(isAutoSearchNode);
            return Response.success(response);
        } catch (Exception e){
            String message = "查询是否允许自动搜索区块链节点失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 开启"自动搜索节点"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<ActiveAutoSearchNodeResponse> activeAutoSearchNode(@RequestBody ActiveAutoSearchNodeRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().activeAutoSearchNode();
            ActiveAutoSearchNodeResponse response = new ActiveAutoSearchNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "开启自动搜索节点选项失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 关闭"自动搜索节点"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeactiveAutoSearchNodeResponse> deactiveAutoSearchNode(@RequestBody DeactiveAutoSearchNodeRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().deactiveAutoSearchNode();
            DeactiveAutoSearchNodeResponse response = new DeactiveAutoSearchNodeResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "关闭自动搜索新区块选项失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * 删除区块
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DELETE_BLOCKS,method={RequestMethod.GET,RequestMethod.POST})
    public Response<DeleteBlocksResponse> deleteBlocks(@RequestBody DeleteBlocksRequest request){
        try {
            blockchainNetCore.getBlockchainCore().deleteBlocks(request.getBlockHeight());
            DeleteBlocksResponse response = new DeleteBlocksResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "删除区块失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }



    /**
     * 获取最大挖矿高度
     */
    @RequestMapping(value = NodeConsoleApplicationApi.GET_MAX_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<GetMaxBlockHeightResponse> getMaxBlockHeight(@RequestBody GetMaxBlockHeightRequest request){
        try {
            long  maxBlockHeight = blockchainNetCore.getBlockchainCore().getMiner().getMaxBlockHeight();
            GetMaxBlockHeightResponse response = new GetMaxBlockHeightResponse();
            response.setMaxBlockHeight(maxBlockHeight);
            return Response.success(response);
        } catch (Exception e){
            String message = "获取[最大挖矿高度]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
    /**
     * 设置最大挖矿高度
     */
    @RequestMapping(value = NodeConsoleApplicationApi.SET_MAX_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public Response<SetMaxBlockHeightResponse> setMaxBlockHeight(@RequestBody SetMaxBlockHeightRequest request){
        try {
            long height = request.getMaxBlockHeight();
            blockchainNetCore.getBlockchainCore().getMiner().setMaxBlockHeight(height);
            SetMaxBlockHeightResponse response = new SetMaxBlockHeightResponse();
            return Response.success(response);
        } catch (Exception e){
            String message = "设置[最大挖矿高度]失败";
            LogUtil.error(message,e);
            return Response.serviceUnavailable();
        }
    }
}