package com.helloworldcoin.netcore.dao;

import com.helloworldcoin.netcore.po.NodePo;

import java.util.List;

/**
 * node dao
 *
 * @author x.king xdotking@gmail.com
 */
public interface NodeDao {

    /**
     * query node
     */
    NodePo queryNode(String ip);
    /**
     * query all nodes
     */
    List<NodePo> queryAllNodes();
    /**
     * add node
     */
    void addNode(NodePo node);
    /**
     * update node
     */
    void updateNode(NodePo node);
    /**
     * delete node
     */
    void deleteNode(String ip);
}
