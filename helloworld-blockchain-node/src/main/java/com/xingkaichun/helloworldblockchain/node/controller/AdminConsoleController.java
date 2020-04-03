package com.xingkaichun.helloworldblockchain.node.controller;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.AdminConsoleApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response.*;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.UpdateAdminUserRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.UpdateAdminUserResponse;
import com.xingkaichun.helloworldblockchain.node.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * admin控制台：用于控制本地区块链节点，如激活矿工、停用矿工、同步其它节点数据等。
 */
@Controller
@RequestMapping
public class AdminConsoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminConsoleController.class);

    @Autowired
    private AdminConsoleService adminConsoleService;

    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Autowired
    private BlockChainBranchService blockChainBranchService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockChainCore blockChainCore;

    /**
     * 矿工是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsMinerActiveResponse> isMineActive(@RequestBody IsMinerActiveRequest request){
        try {
            boolean isMineActive = adminConsoleService.isMinerActive();

            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMinerInActiveState(isMineActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否处于激活状态成功",response);
        } catch (Exception e){
            String message = "查询矿工是否处于激活状态失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveMinerResponse> activeMiner(@RequestBody ActiveMinerRequest request){
        try {
            adminConsoleService.activeMiner();
            ActiveMinerResponse response = new ActiveMinerResponse();
            response.setActiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活矿工成功",response);
        } catch (Exception e){
            String message = "激活矿工失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveMinerResponse> deactiveMiner(@RequestBody DeactiveMinerRequest request){
        try {
            adminConsoleService.deactiveMiner();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            response.setDeactiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用矿工成功",response);
        } catch (Exception e){
            String message = "停用矿工失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 同步器是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_SYNCHRONIZER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsSynchronizerActiveResponse> isSynchronizerActive(@RequestBody IsSynchronizerActiveRequest request){
        try {
            boolean isSynchronizerActive = adminConsoleService.isSynchronizerActive();

            IsSynchronizerActiveResponse response = new IsSynchronizerActiveResponse();
            response.setSynchronizerInActiveState(isSynchronizerActive);
            return ServiceResult.createSuccessServiceResult("查询同步器是否激活成功",response);
        } catch (Exception e){
            String message = "查询同步器是否激活失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活同步器
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveSynchronizerResponse> activeSynchronizer(@RequestBody ActiveSynchronizerRequest request){
        try {
            adminConsoleService.activeSynchronizer();
            ActiveSynchronizerResponse response = new ActiveSynchronizerResponse();
            response.setActiveSynchronizerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活同步器成功",response);
        } catch (Exception e){
            String message = "激活同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用同步器
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveSynchronizerResponse> deactiveSynchronizer(@RequestBody DeactiveSynchronizerRequest request){
        try {
            adminConsoleService.deactiveSynchronizer();
            DeactiveSynchronizerResponse response = new DeactiveSynchronizerResponse();
            response.setDeactiveSynchronizerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用同步器成功",response);
        } catch (Exception e){
            String message = "停用同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 更换当前区块链分支
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_BLOCKCHAINBRANCH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateBlockchainBranchResponse> updateBranchchainBranch(@RequestBody UpdateBlockchainBranchRequest request){
        try {
            blockChainBranchService.updateBranchchainBranch(request.getBlockList());
            UpdateBlockchainBranchResponse response = new UpdateBlockchainBranchResponse();
            return ServiceResult.createSuccessServiceResult("成功更换当前区块链分支",response);
        } catch (Exception e){
            String message = "更换当前区块链分支失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 更新系统管理员用户
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_ADMIN_USER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateAdminUserResponse> updateAdminUserRequest(@RequestBody UpdateAdminUserRequest request){
        try {
            userService.updateUser(request.getUserDto());
            UpdateAdminUserResponse response = new UpdateAdminUserResponse();
            return ServiceResult.createSuccessServiceResult("更新系统用户成功",response);
        } catch (Exception e){
            String message = "更新系统用户失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询矿工的地址
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.QUERY_MINER_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMinerAddressResponse> queryMinerAddress(@RequestBody QueryMinerAddressRequest request){
        try {
            String minerAddress = configurationService.getMinerAddress();
            QueryMinerAddressResponse response = new QueryMinerAddressResponse();
            response.setMinerAddress(minerAddress);
            return ServiceResult.createSuccessServiceResult("查询矿工的地址成功",response);
        } catch (Exception e){
            String message = "查询矿工的地址失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 设置矿工地址
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_MINER_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetMinerAddressResponse> setMinerAddress(@RequestBody SetMinerAddressRequest request){
        try {
            if(adminConsoleService.isMinerActive()){
                return ServiceResult.createFailServiceResult("矿工正在挖矿，请先暂停挖矿，再设置矿工钱包地址");
            }
            blockChainCore.getMiner().setMinerStringAddress(new StringAddress(request.getMinerAddress()));
            configurationService.setMinerAddress(request.getMinerAddress());
            SetMinerAddressResponse response = new SetMinerAddressResponse();
            return ServiceResult.createSuccessServiceResult("成功重置矿工的钱包地址！",response);
        } catch (Exception e){
            String message = "重置矿工的地址失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 新增节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            Node node = request.getNode();
            if(Strings.isNullOrEmpty(node.getIp())){
                return ServiceResult.createFailServiceResult("节点IP不能为空");
            }
            if(Strings.isNullOrEmpty(node.getIp())){
                return ServiceResult.createFailServiceResult("节点端口不能为空");
            }
            if(nodeService.queryNode(node) != null){
                return ServiceResult.createFailServiceResult("节点已经存在，不需要重复添加");
            }
            nodeService.addNode(node);
            AddNodeResponse response = new AddNodeResponse();
            response.setAddNodeSuccess(true);
            return ServiceResult.createSuccessServiceResult("新增节点成功",response);
        } catch (Exception e){
            String message = "新增节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 更新节点信息
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateNodeResponse> updateNode(@RequestBody UpdateNodeRequest request){
        try {
            if(request.getNode() == null){
                return ServiceResult.createFailServiceResult("请填写节点信息");
            }
            if(nodeService.queryNode(request.getNode()) == null){
                return ServiceResult.createFailServiceResult("节点不存在，无法更新");
            }
            nodeService.updateNode(request.getNode());
            UpdateNodeResponse response = new UpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点信息成功",response);
        } catch (Exception e){
            String message = "更新节点信息失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 删除节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DELETE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteNodeResponse> deleteNode(@RequestBody DeleteNodeRequest request){
        try {
            nodeService.deleteNode(request.getNode());
            DeleteNodeResponse response = new DeleteNodeResponse();
            return ServiceResult.createSuccessServiceResult("删除节点成功",response);
        } catch (Exception e){
            String message = "删除节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.QUERY_NODE_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryNodeListResponse> queryNodeList(@RequestBody QueryNodeListRequest request){
        try {
            List<Node> nodeList = nodeService.queryNodeList(request);
            QueryNodeListResponse response = new QueryNodeListResponse();
            response.setNodeList(nodeList);
            return ServiceResult.createSuccessServiceResult("查询节点成功",response);
        } catch (Exception e){
            String message = "查询节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询是否允许自动搜索区块链节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsAutoSearchNodeResponse> isAutoSearchNewNode(@RequestBody IsAutoSearchNodeRequest request){
        try {
            boolean autoSearchNode = configurationService.autoSearchNode();
            IsAutoSearchNodeResponse response = new IsAutoSearchNodeResponse();
            response.setAutoSearchNewNode(autoSearchNode);
            return ServiceResult.createSuccessServiceResult("查询是否允许自动搜索区块链节点成功",response);
        } catch (Exception e){
            String message = "查询是否允许自动搜索区块链节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 设置否允许自动搜索区块链节点开关
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetAutoSearchNodeResponse> setAutoSearchNode(@RequestBody SetAutoSearchNodeRequest request){
        try {
            configurationService.setAutoSearchNode(request.isAutoSearchNode());
            SetAutoSearchNodeResponse response = new SetAutoSearchNodeResponse();
            return ServiceResult.createSuccessServiceResult("设置否允许自动搜索区块链节点开关成功",response);
        } catch (Exception e){
            String message = "设置否允许自动搜索区块链节点开关失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 删除区块
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.REMOVE_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<RemoveBlockResponse> removeBlock(@RequestBody RemoveBlockRequest request){
        try {
            if(request.getBlockHeight() == null){
                return ServiceResult.createFailServiceResult("删除区块失败，区块高度不能空。");
            }
            blockChainCoreService.removeBlocksUtilBlockHeightLessThan(request.getBlockHeight());
            RemoveBlockResponse response = new RemoveBlockResponse();
            return ServiceResult.createSuccessServiceResult("删除区块成功",response);
        } catch (Exception e){
            String message = "删除区块失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }




    //region 配置
    /**
     * 本地节点发现某一个节点错误次数过多，则删除该节点。获取这个次数阈值。
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.GET_NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GetNodeErrorConnectionTimesRemoveThresholdResponse> getNodeErrorConnectionTimesRemoveThreshold(@RequestBody GetNodeErrorConnectionTimesRemoveThresholdRequest request){
        try {
            long nodeErrorConnectionTimesRemoveThreshold = configurationService.getNodeErrorConnectionTimesRemoveThreshold();
            GetNodeErrorConnectionTimesRemoveThresholdResponse response = new GetNodeErrorConnectionTimesRemoveThresholdResponse();
            response.setNodeErrorConnectionTimesRemoveThreshold(nodeErrorConnectionTimesRemoveThreshold);
            return ServiceResult.createSuccessServiceResult("本地节点发现某一个节点错误次数过多，则删除该节点。获取这个次数阈值成功",response);
        } catch (Exception e){
            String message = "本地节点发现某一个节点错误次数过多，则删除该节点。获取这个次数阈值失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 本地节点发现某一个节点错误次数过多，则删除该节点。设置这个次数阈值。
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetNodeErrorConnectionTimesRemoveThresholdResponse> setNodeErrorConnectionTimesRemoveThreshold(@RequestBody SetNodeErrorConnectionTimesRemoveThresholdRequest request){
        try {
            long nodeErrorConnectionTimesRemoveThreshold = request.getNodeErrorConnectionTimesRemoveThreshold();
            if(nodeErrorConnectionTimesRemoveThreshold <= 0){
                return ServiceResult.createFailServiceResult("参数必须为正整数");
            }
            configurationService.setNodeErrorConnectionTimesRemoveThreshold(request.getNodeErrorConnectionTimesRemoveThreshold());
            SetNodeErrorConnectionTimesRemoveThresholdResponse response = new SetNodeErrorConnectionTimesRemoveThresholdResponse();
            return ServiceResult.createSuccessServiceResult("本地节点发现某一个节点错误次数过多，则删除该节点。设置这个次数阈值成功",response);
        } catch (Exception e){
            String message = "本地节点发现某一个节点错误次数过多，则删除该节点。设置这个次数阈值失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 获取[在区块链网络中自动搜寻新的节点]的间隔时间
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.GET_NODE_SEARCH_NEW_NODE_TIME_INTERVAL,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GetNodeSearchNewNodeTimeIntervalResponse> getNodeSearchNewNodeTimeInterval(@RequestBody GetNodeSearchNewNodeTimeIntervalRequest request){
        try {
            long nodeSearchNewNodeTimeInterval = configurationService.getNodeSearchNewNodeTimeInterval();
            GetNodeSearchNewNodeTimeIntervalResponse response = new GetNodeSearchNewNodeTimeIntervalResponse();
            response.setNodeSearchNewNodeTimeInterval(nodeSearchNewNodeTimeInterval);
            return ServiceResult.createSuccessServiceResult("获取[在区块链网络中自动搜寻新的节点]的间隔时间成功",response);
        } catch (Exception e){
            String message = "获取[在区块链网络中自动搜寻新的节点]的间隔时间节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 设置[在区块链网络中自动搜寻新的节点]的间隔时间
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_SEARCH_NEW_BLOCKS_TIME_INTERVAL,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetSearchNewBlocksTimeIntervalResponse> setSearchNewBlocksTimeInterval(@RequestBody SetSearchNewBlocksTimeIntervalRequest request){
        try {
            long searchNewBlocksTimeInterval = request.getSearchNewBlocksTimeInterval();
            if(searchNewBlocksTimeInterval <= 0){
                return ServiceResult.createFailServiceResult("参数必须为正整数");
            }
            configurationService.setSearchNewBlocksTimeInterval(searchNewBlocksTimeInterval);
            SetSearchNewBlocksTimeIntervalResponse response = new SetSearchNewBlocksTimeIntervalResponse();
            return ServiceResult.createSuccessServiceResult("设置[在区块链网络中自动搜寻新的节点]的间隔时间成功",response);
        } catch (Exception e){
            String message = "设置[在区块链网络中自动搜寻新的节点]的间隔时间失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 获取[在区块链网络中自动搜寻新的区块]的间隔时间
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.GET_SEARCH_NEW_BLOCKS_TIME_INTERVAL,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GetSearchNewBlocksTimeIntervalResponse> getSearchNewBlocksTimeInterval(@RequestBody GetSearchNewBlocksTimeIntervalRequest request){
        try {
            long searchNewBlocksTimeInterval = configurationService.getSearchNewBlocksTimeInterval();
            GetSearchNewBlocksTimeIntervalResponse response = new GetSearchNewBlocksTimeIntervalResponse();
            response.setSearchNewBlocksTimeInterval(searchNewBlocksTimeInterval);
            return ServiceResult.createSuccessServiceResult("查询[在区块链网络中自动搜寻新的区块]的间隔时间成功",response);
        } catch (Exception e){
            String message = "查询[在区块链网络中自动搜寻新的区块]的间隔时间失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 设置[在区块链网络中自动搜寻新的区块]的间隔时间
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_NODE_SEARCH_NEW_NODE_TIME_INTERVAL,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetNodeSearchNewNodeTimeIntervalResponse> setNodeSearchNewNodeTimeInterval(@RequestBody SetNodeSearchNewNodeTimeIntervalRequest request){
        try {
            long nodeSearchNewNodeTimeInterval = request.getNodeSearchNewNodeTimeInterval();
            if(nodeSearchNewNodeTimeInterval <= 0){
                return ServiceResult.createFailServiceResult("参数必须为正整数");
            }
            configurationService.setNodeSearchNewNodeTimeInterval(nodeSearchNewNodeTimeInterval);
            SetNodeSearchNewNodeTimeIntervalResponse response = new SetNodeSearchNewNodeTimeIntervalResponse();
            return ServiceResult.createSuccessServiceResult("设置[在区块链网络中自动搜寻新的区块]的间隔时间成功",response);
        } catch (Exception e){
            String message = "设置[在区块链网络中自动搜寻新的区块]的间隔时间失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 获取时间间隔
     * 检查自己的区块链高度在区块链网络中是否是最大的高度的时间间隔
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.GET_CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsAutoSearchNode4Response> getCheckLocalBlockChainHeightIsHighTimeInterval(@RequestBody IsAutoSearchNode4Request request){
        try {
            long checkLocalBlockChainHeightIsHighTimeInterval = configurationService.getCheckLocalBlockChainHeightIsHighTimeInterval();
            IsAutoSearchNode4Response response = new IsAutoSearchNode4Response();
            response.setCheckLocalBlockChainHeightIsHighTimeInterval(checkLocalBlockChainHeightIsHighTimeInterval);
            return ServiceResult.createSuccessServiceResult("查询[检查自己的区块链高度在区块链网络中是否是最大的高度]的时间间隔",response);
        } catch (Exception e){
            String message = "查询[检查自己的区块链高度在区块链网络中是否是最大的高度]的时间间隔失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 设置时间间隔
     * [检查自己的区块链高度在区块链网络中是否是最大的高度]的时间间隔
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetCheckLocalBlockChainHeightIsHighTimeIntervalResponse> setCheckLocalBlockChainHeightIsHighTimeInterval(@RequestBody SetCheckLocalBlockChainHeightIsHighTimeIntervalRequest request){
        try {
            long checkLocalBlockChainHeightIsHighTimeInterval = request.getCheckLocalBlockChainHeightIsHighTimeInterval();
            if(checkLocalBlockChainHeightIsHighTimeInterval <= 0){
                return ServiceResult.createFailServiceResult("参数必须为正整数");
            }
            configurationService.setCheckLocalBlockChainHeightIsHighTimeInterval(checkLocalBlockChainHeightIsHighTimeInterval);
            SetCheckLocalBlockChainHeightIsHighTimeIntervalResponse response = new SetCheckLocalBlockChainHeightIsHighTimeIntervalResponse();
            return ServiceResult.createSuccessServiceResult("查询[检查自己的区块链高度在区块链网络中是否是最大的高度]的时间间隔成功",response);
        } catch (Exception e){
            String message = "查询[检查自己的区块链高度在区块链网络中是否是最大的高度]的时间间隔失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    //endregion

}