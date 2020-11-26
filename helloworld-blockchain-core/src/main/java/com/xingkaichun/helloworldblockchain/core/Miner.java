package com.xingkaichun.helloworldblockchain.core;

/**
 * 矿工:挖矿、分配挖矿奖励、将挖取的区块放入区块链
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class Miner {

    //矿工钱包
    protected Wallet wallet;
    //矿工挖矿所在的区块链
    protected BlockchainDatabase blockchainDataBase;
    //矿工交易数据库：矿工从交易数据库里获取挖矿的原材料(交易数据)
    protected MinerTransactionDtoDatabase minerTransactionDtoDataBase;

    public Miner(Wallet wallet, BlockchainDatabase blockchainDataBase, MinerTransactionDtoDatabase minerTransactionDtoDataBase) {
        this.wallet = wallet;
        this.blockchainDataBase = blockchainDataBase;
        this.minerTransactionDtoDataBase = minerTransactionDtoDataBase;
    }


    //region 挖矿相关
    /**
     * 启用矿工。
     * 矿工有两种状态：活动状态与非活动状态。
     * 若矿工处于活动作态，开始挖矿。
     * 若矿工处于非活动状态，矿工不会进行任何工作。
     */
    public abstract void start() ;

    /**
     * 矿工是否处于活动状态。
     */
    public abstract boolean isActive() ;

    /**
     * 激活矿工：设置矿工为活动状态。
     */
    public abstract void active() ;

    /**
     * 停用矿工：设置矿工为非活动状态。
     */
    public abstract void deactive() ;
    //endregion


    //region get set


    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public BlockchainDatabase getBlockchainDataBase() {
        return blockchainDataBase;
    }

    public MinerTransactionDtoDatabase getMinerTransactionDtoDataBase() {
        return minerTransactionDtoDataBase;
    }
    //endregion
}