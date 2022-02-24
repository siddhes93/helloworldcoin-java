package com.helloworldcoin.netcore.service;

import com.helloworldcoin.netcore.model.Node;

import java.util.List;

/**
 * node service
 *
 * @author x.king xdotking@gmail.com
 */
public interface NodeService {
    /**
     * query node
     */
    Node queryNode(String ip);
    /**
     * query all nodes
     */
    List<Node> queryAllNodes();

    /**
     * delete node
     */
    void deleteNode(String ip);

    /**
     * add node
     */
    void addNode(Node node);

    /**
     * update node
     */
    void updateNode(Node node);
}
