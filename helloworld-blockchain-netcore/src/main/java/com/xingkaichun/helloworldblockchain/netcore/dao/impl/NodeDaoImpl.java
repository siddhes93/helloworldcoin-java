package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.netcore.configuration.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.po.NodePo;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import com.xingkaichun.helloworldblockchain.util.EncodeDecodeTool;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeDaoImpl implements NodeDao {

    private static final String NODE_DATABASE_NAME = "NodeDatabase";
    private NetCoreConfiguration netCoreConfiguration;

    public NodeDaoImpl(NetCoreConfiguration netCoreConfiguration) {
        this.netCoreConfiguration = netCoreConfiguration;
    }

    @Override
    public NodePo queryNode(String ip){
        byte[] bytesNodePo = KvDbUtil.get(getNodeDatabasePath(),getKeyByIp(ip));
        if(bytesNodePo == null){
            return null;
        }
        return EncodeDecodeTool.decode(bytesNodePo,NodePo.class);
    }

    @Override
    public void addNode(NodePo node){
        KvDbUtil.put(getNodeDatabasePath(),getKeyByNodePo(node), EncodeDecodeTool.encode(node));
    }

    @Override
    public void updateNode(NodePo node){
        KvDbUtil.put(getNodeDatabasePath(),getKeyByNodePo(node),EncodeDecodeTool.encode(node));
    }

    @Override
    public void deleteNode(String ip){
        KvDbUtil.delete(getNodeDatabasePath(),getKeyByIp(ip));
    }

    @Override
    public List<NodePo> queryAllNodes(){
        List<NodePo> nodePos = new ArrayList<>();
        //获取所有
        List<byte[]> bytesNodePos = KvDbUtil.gets(getNodeDatabasePath(),1,100000000);
        if(bytesNodePos != null){
            for(byte[] bytesNodePo:bytesNodePos){
                NodePo nodePo = EncodeDecodeTool.decode(bytesNodePo,NodePo.class);
                nodePos.add(nodePo);
            }
        }
        return nodePos;
    }
    private String getNodeDatabasePath(){
        return FileUtil.newPath(netCoreConfiguration.getNetCorePath(), NODE_DATABASE_NAME);
    }
    private byte[] getKeyByNodePo(NodePo node){
        return getKeyByIp(node.getIp());
    }
    private byte[] getKeyByIp(String ip){
        return ByteUtil.stringToUtf8Bytes(ip);
    }
}
