package com.xingkaichun.helloworldblockchain.setting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 全局设置
 *
 * @author 邢开春
 */
public class GlobalSetting {

    //区块链网络默认的节点端口
    public static final int DEFAULT_PORT = 8888;
    //区块链网络中的种子节点
    public static final List<String> SEED_NODE_LIST = Arrays.asList("139.9.125.122","119.3.57.171");


    /**
     * 系统版本，这里的版本是一个区块高度
     * 要求：[区块的高度]必须要小于[区块链版本]，系统才能正常运行。
     */
    public static class SystemVersionConstant{
        /**
         * 版本列表
         */
        public static final List<Long> BLOCK_CHAIN_VERSION_LIST =
                Collections.unmodifiableList(Arrays.asList(10000L));

        /**
         * 检查系统版本是否支持。
         */
        public static boolean isVersionLegal(long blockHeight){
            return blockHeight <= BLOCK_CHAIN_VERSION_LIST.get(BLOCK_CHAIN_VERSION_LIST.size() - 1);
        }
    }

    /**
     * 创世区块
     */
    public static class GenesisBlock{
        //创世区块的高度
        public static final long HEIGHT = 0;
        //创世区块的哈希
        public static final String HASH = "0000000000000000000000000000000000000000000000000000000000000000";
        /**
         * 创世区块的挖矿难度，约为我的华为开发笔记本10分钟工作量。
         */
        public static final String DIFFICULTY = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

    }

    /**
     * 挖矿设置
     */
    public static class MinerConstant{
        //每个区块平均产生时间
        public static final long GENERATE_BLOCK_AVERAGE_TIMESTAMP = 1000 *  60 * 2;
        //挖矿激励减产的周期
        public static final long MINE_BLOCK_INCENTIVE_REDUCE_BY_HALF_INTERVAL_TIMESTAMP = GENERATE_BLOCK_AVERAGE_TIMESTAMP * 2;

        //初始化挖矿激励金额
        public static final long INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT = 50L;

        //每轮挖矿最大时长。挖矿时间太长，则新提交的交易就很延迟才能包含到区块里。
        public static final long MINE_TIMESTAMP_PER_ROUND = 1000 * 60 * 2;
    }

    /**
     * 区块设置
     */
    public static class BlockConstant {
        //区块最多含有的交易数量
        public static final long BLOCK_MAX_TRANSACTION_COUNT = 1024;
        //交易文本字符串最大长度值 TODO 1024能有几笔交易输出？
        public static final long TRANSACTION_TEXT_MAX_SIZE = 1024 * 1024;
        //区块存储容量限制
        public static final long BLOCK_TEXT_MAX_SIZE = TRANSACTION_TEXT_MAX_SIZE * BLOCK_MAX_TRANSACTION_COUNT;
        //nonce的长度  32字节
        public static final long NONCE_TEXT_SIZE = 32;
    }

    /**
     * 脚本设置
     */
    public static class ScriptConstant{
        //脚本最大存储容量
        public static final long SCRIPT_TEXT_MAX_SIZE = 1024;
    }

    /**
     * 节点设置
     */
    public static class NodeConstant{
        //两个区块链有分叉时，区块差异数量大于这个值，则真的分叉了。
        public static final long FORK_BLOCK_SIZE = 100;
        //在区块链网络中自动搜寻新的节点的间隔时间
        public static final long SEARCH_NEW_NODE_TIME_INTERVAL = 1000 * 60 * 2;
        //本地节点发现某一个节点错误次数过多，则删除该节点。这个阈值配置。
        public static final long NODE_ERROR_CONNECTION_TIMES_DELETE_THRESHOLD = 10;
        //在区块链网络中自动搜寻新的区块的间隔时间。
        public static final long SEARCH_NEW_BLOCKS_TIME_INTERVAL = 1000 * 60;
        //检查自己的区块链高度在区块链网络中是否是最大的高度的时间间隔。
        public static final long CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL = 1000 * 60;
        //定时将种子节点加入本地区块链网络的时间间隔。
        public static final long ADD_SEED_NODE_TIME_INTERVAL = 1000 * 60 * 60;
        //广播自己节点的时间间隔。
        public static final long NODE_BROADCAST_TIME_INTERVAL = 1000 * 60* 60 * 24;
        //搜寻区块的时间间隔。
        public static final long BLOCK_SEARCH_TIME_INTERVAL = 1000 * 60 * 2;
    }
}
