package com.helloworldcoin.netcore.dao;

import com.helloworldcoin.netcore.po.NodePo;

import java.util.List;

/**
 * 节点dao
 * 管理（增删改查）已知的网络节点。
 *
 * @author x.king xdotking@gmail.com
 */
public interface NodeDao {

    /**
     * 查询节点
     */
    NodePo queryNode(String ip);
    /**
     * 查询所有节点
     */
    List<NodePo> queryAllNodes();
    /**
     * 添加节点
     */
    void addNode(NodePo node);
    /**
     * 更新节点信息
     */
    void updateNode(NodePo node);
    /**
     * 删除节点
     */
    void deleteNode(String ip);
}
