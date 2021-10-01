package com.xingkaichun.helloworldblockchain.netcore.configuration;


/**
 * NetCore配置: BlockchainNetCore的配置。
 * 该类对BlockchainNetCore模块的配置进行统一管理。
 * 在这里可以持久化配置信息。
 * 理论上，BlockchainNetCore模块的任何地方需要配置参数，都可以从该类获取。
 *
 * @author 邢开春 409060350@qq.com
 */
public interface NetCoreConfiguration {

    /**
     * BlockchainNetCore数据的存储路径
     */
    String getNetCorePath();


    /**
     * 种子节点初始化时间间隔
     */
    long getSeedNodeInitializeTimeInterval();
    /**
     * 节点搜索时间间隔
     */
    long getNodeSearchTimeInterval();
    /**
     * 节点广播时间间隔
     */
    long getNodeBroadcastTimeInterval();
    /**
     * 节点清理时间间隔
     */
    long getNodeCleanTimeInterval();


    /**
     * 是否"自动搜索新区块"
     */
    boolean isAutoSearchBlock();
    /**
     * 开启"自动搜索新区块"选项
     */
    void activeAutoSearchBlock() ;
    /**
     * 关闭"自动搜索新区块"选项
     */
    void deactiveAutoSearchBlock() ;


    /**
     * 是否自动搜索节点
     */
    boolean isAutoSearchNode();
    /**
     * 开启自动搜索节点
     */
    void activeAutoSearchNode();
    /**
     * 关闭自动搜索节点
     */
    void deactiveAutoSearchNode();


    /**
     * 区块搜索时间间隔
     */
    long getBlockSearchTimeInterval();
    /**
     * 区块广播时间间隔
     */
    long getBlockBroadcastTimeInterval();


    /**
     * 区块链高度搜索时间间隔
     */
    long getBlockchainHeightSearchTimeInterval();
    /**
     * 区块链高度广播时间间隔
     */
    long getBlockchainHeightBroadcastTimeInterval();


    /**
     * 硬分叉区块数量：两个区块链有分叉时，区块差异数量大于这个值，则真的分叉了。
     */
    long getHardForkBlockCount();

    /**
     * 未确认交易的搜索时间间隔
     */
    long getUnconfirmedTransactionsSearchTimeInterval();
}
