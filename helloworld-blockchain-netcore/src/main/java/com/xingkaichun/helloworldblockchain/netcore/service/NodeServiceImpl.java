package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.po.NodePo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeServiceImpl implements NodeService {

    private NodeDao nodeDao;

    public NodeServiceImpl(NodeDao nodeDao) {
        this.nodeDao = nodeDao;
    }

    @Override
    public void deleteNode(String ip){
        nodeDao.deleteNode(ip);
    }

    @Override
    public List<Node> queryAllNodes(){
        List<NodePo> nodePos = nodeDao.queryAllNodes();
        return nodePo2Nodes(nodePos);
    }

    @Override
    public void addNode(Node node){
        if(nodeDao.queryNode(node.getIp()) != null){
            return;
        }
        NodePo nodePo = node2NodePo(node);
        nodeDao.addNode(nodePo);
    }

    @Override
    public void updateNode(Node node){
        NodePo nodePo = nodeDao.queryNode(node.getIp());
        if(nodePo == null){
            return;
        }
        nodePo = node2NodePo(node);
        nodeDao.updateNode(nodePo);
    }

    @Override
    public Node queryNode(String ip){
        NodePo nodePo = nodeDao.queryNode(ip);
        if(nodePo == null){
            return null;
        }
        return nodePo2Node(nodePo);
    }

    private List<Node> nodePo2Nodes(List<NodePo> nodePos){
        List<Node> nodeList = new ArrayList<>();
        if(nodePos != null){
            for(NodePo nodePo : nodePos){
                Node node = nodePo2Node(nodePo);
                nodeList.add(node);
            }
        }
        return nodeList;
    }
    private Node nodePo2Node(NodePo nodePo){
        Node node = new Node();
        node.setIp(nodePo.getIp());
        node.setBlockchainHeight(nodePo.getBlockchainHeight());
        return node;
    }
    private NodePo node2NodePo(Node node){
        NodePo nodePo = new NodePo();
        nodePo.setIp(node.getIp());
        nodePo.setBlockchainHeight(node.getBlockchainHeight());
        return nodePo;
    }

}
