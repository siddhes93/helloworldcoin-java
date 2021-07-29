package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.PingRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;


/**
 * 节点清理器：清除死亡节点。
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeCleaner {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeCleaner(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                cleanDeadNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeCleanTimeInterval());
            }
        } catch (Exception e) {
            SystemUtil.errorExit("在区块链网络中广播自己出现异常",e);
        }
    }

    private void cleanDeadNodes() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest);
            if(pingResponse == null){
                nodeService.deleteNode(node.getIp());
                LogUtil.debug("节点清理器发现死亡节点["+node+"]，节点已从节点数据库删除。");
            }
        }
    }

}
