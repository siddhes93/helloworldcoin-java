package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.PingRequest;
import com.xingkaichun.helloworldblockchain.netcore.dto.PingResponse;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.configuration.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;


/**
 * 节点清理器：清除死亡节点。
 * 所谓死亡节点就是无法联系的节点。
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
            SystemUtil.errorExit("清理死亡节点出现异常",e);
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
                LogUtil.debug("节点清理器发现死亡节点["+node.getIp()+"]，已在节点数据库中将该节点删除了。");
            }
        }
    }

}
