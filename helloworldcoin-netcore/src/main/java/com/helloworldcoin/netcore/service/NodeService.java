package com.helloworldcoin.netcore.service;

import com.helloworldcoin.netcore.model.Node;

import java.util.List;

/**
 * node service
 *
 * @author x.king xdotking@gmail.com
 */
public interface NodeService {

    Node queryNode(String ip);

    List<Node> queryAllNodes();

    void deleteNode(String ip);

    void addNode(Node node);

    void updateNode(Node node);
}
