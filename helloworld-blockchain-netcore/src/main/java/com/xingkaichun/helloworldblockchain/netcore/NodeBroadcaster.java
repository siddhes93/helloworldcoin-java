package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.client.NodeClient;
import com.xingkaichun.helloworldblockchain.netcore.client.NodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.PingRequest;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.configuration.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;

import java.util.List;


/**
 * 节点广播器：在区块链网络中，广播自身这个节点。
 * 为什么要广播自身这个节点？
 * 例如，系统启动后，广播自身，让区块链网络的其它节点知道自己已经上线了。
 * 再例如，由于未知原因，部分节点与自己中断了联系，自己已经不在它们的节点列表中了，
 * 而自己的列表中有它们，这时候可以广播一下自己，这些中断联系的节点将会恢复与自己联系。
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeBroadcaster {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeBroadcaster(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastNode();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeBroadcastTimeInterval());
            }
        } catch (Exception e) {
            SystemUtil.errorExit("在区块链网络中广播自己出现异常",e);
        }
    }

    /**
     * 广播自己
     */
    private void broadcastNode() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            NodeClient nodeClient = new NodeClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            nodeClient.pingNode(pingRequest);
        }
    }

}
