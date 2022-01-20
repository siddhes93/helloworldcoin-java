package com.helloworldcoin.netcore;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.setting.NetworkSetting;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.ThreadUtil;


/**
 * 种子节点初始化器
 * 春种一粒粟,秋收万颗子。春天播种一粒种子节点,秋天就可以收获很多的节点了。
 * 有了这粒种子节点，我们(节点搜寻器NodeSearcher)就可以向种子节点询问：“种子节点，种子节点，你知道其它存在的节点吗？”。
 * 种子节点就会回复：“我知道，有节点甲、节点乙、节点丙...”。同时，种子节点还会把你这个节点记录下来。
 * 有了节点甲，我们(节点搜寻器NodeSearcher)就可以向甲节点询问：“甲节点，甲节点，你知道其它存在的节点吗？”。同时，甲节点还会把你这个节点记录下来。
 * 有了节点乙，我们(节点搜寻器NodeSearcher)就可以向乙节点询问：“乙节点，乙节点，你知道其它存在的节点吗？”。同时，乙节点还会把你这个节点记录下来。
 * ...
 * 最终我们将知道所有的节点。
 * 同理，任一节点将知道所有的节点。
 * 所以最终任意两个节点都将互相知道对方的存在，并互联起来，组成了区块链网络。
 *
 * @author x.king xdotking@gmail.com
 */
public class SeedNodeInitializer {

    private NetCoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public SeedNodeInitializer(NetCoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    //TODO 提供两个方法 start and run ？
    public void start() {
        try {
            while (true){
                addSeedNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getSeedNodeInitializeTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("定时将种子节点加入区块链网络出现异常",e);
        }
    }

    /**
     * 添加种子节点
     */
    private void addSeedNodes() {
        if(!netCoreConfiguration.isAutoSearchNode()){
            return;
        }

        for(String seedNode: NetworkSetting.SEED_NODES){
            Node node = new Node();
            node.setIp(seedNode);
            node.setBlockchainHeight(0);
            nodeService.addNode(node);
            LogUtil.debug("种子节点初始化器提示您:种子节点["+node.getIp()+"]加入了区块链网络。");
        }
    }

}
