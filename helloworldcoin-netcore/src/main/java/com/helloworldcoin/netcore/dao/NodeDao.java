package com.helloworldcoin.netcore.dao;

import com.helloworldcoin.netcore.po.NodePo;

import java.util.List;

/**
 * node dao
 *
 * @author x.king xdotking@gmail.com
 */
public interface NodeDao {


    NodePo queryNode(String ip);

    List<NodePo> queryAllNodes();

    void addNode(NodePo node);

    void updateNode(NodePo node);

    void deleteNode(String ip);
}
