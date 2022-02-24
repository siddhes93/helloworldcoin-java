package com.helloworldcoin.netcore.dao.impl;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.po.NodePo;
import com.helloworldcoin.netcore.dao.NodeDao;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.EncodeDecodeTool;
import com.helloworldcoin.util.FileUtil;
import com.helloworldcoin.util.KvDbUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
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
