package com.xingkaichun.helloworldblockchain.core.model;


import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * 区块
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class Block implements Serializable {

    /**
     * 区块产生的时间戳
     * 这里约定：区块的时间戳一定比前一个区块的时间戳大，一定比当前时间小。
     * 为什么需要时间戳这个字段？
     * 这个时间戳很有用处，当然没人规定区块链一定要有这个字段，拿掉它也是可以的。以下列举一下这个字段可能的用处:
     * 记录区块形成的时间。
     * 记录了区块的时间，可以利用这个值用于估算区块产生的速度，动态调整区块产生的难度。
     * 区块不允许放入超过一定时间产生的交易(例如一年前...)。
     */
    private long timestamp;
    /**
     * 区块高度
     */
    private BigInteger height;
    /**
     * 上一个区块的哈希
     */
    private String previousBlockHash;
    /**
     * 区块里的交易
     */
    private List<Transaction> transactions;
    /**
     * 默克尔树根
     * 由transactions生成。
     * 既然这个字段是有由交易列表生成的，这个字段每次需要时完全可以自己生成？为什么需要这个字段？请参考SPV。
     *
     * 冗余字段，这个值可以由区块链计算出来
     */
    private String merkleRoot;
    /**
     * 共识值
     * 区块链是个分布式账本，每个人都拥有记账的权利。如果每个人都很简单的就能够记账，账本就会特别难达成一致。
     * 一般情况下，想要记账请解答一个难题，从而增大记账的难度。
     * 而这个共识值就是难题的答案。难题的答案可能并不是唯一，一般情况下，很难获取答案。
     */
    private String consensusValue;
    /**
     * 区块哈希：由timestamp、previousBlockHash、height、consensusValue、merkleRoot共同作用使用Hash算法生成。
     * 为什么需要哈希这个字段？
     * 区块哈希确定了，反过来说其它字段的值也是确定的，也就是说区块的数据是确定的。
     * 我们知道区块链的区块需要指向上一个区块？怎么指向？
     * 我们就在区块里加上previousBlockHash这个字段，这样上一个区块哈希确定了，自然上一个区块的数据也就确定了。
     * 自然上上一个区块的数据也就确定了，自然所有区块的数据也就确定了。
     * 可以想象，如果我们得到一个区块链的最后一个区块的哈希，那一个区块链的所有数据都是确定下来了，谁也不能伪造。
     *
     * 如果不对区块产生的hash做约束的话，同一区块链上不同的位置区块可能产生相同的hash。
     * 为了简单，这里强制约定：同一区块链上不出现区块hash相同的两个区块。
     * 请保证区块链上不会出现相同的区块hash.
     *
     * 区块哈希，由矿工产生，区块链系统进行唯一性校验、格式校验。
     * 这个值的构成应当足够简单去验证这个值是否是唯一的。
     * 当区块数据足够庞大时，用户节点只有最近一部分区块与UTXO数据，这时节点必须也可以校验它的唯一性。
     * 这里建议它的构成是一串字符+时间戳。
     * 因为后产生的区块时间戳更大，因此只要校验这个时间戳是否比上一个区块产生的时间戳更大，就可校验它的唯一性。
     *
     * 冗余字段，这个值可以由区块链计算出来
     */
    private String hash;

    /**
     * 计算共识的中间变量持有者
     * 这个字段是冗余的。可以通过区块链系统计算出来。用于辅助共识计算。
     * 在做共识计算时，可能会产生很多的中间变量，如果每次都重新计算一次，比较浪费算力，
     * 这里的设计是将计算好的中间变量保存到这个持有者中，下次计算共识，直接从持有者中获取中间变量。
     *
     * 冗余字段，这个值可以由区块链计算出来
     */
    private ConsensusVariableHolder consensusVariableHolder;

    /**
     * 区块中的交易总笔数
     *
     * 冗余字段，这个值可以由区块链计算出来
     */
    private BigInteger transactionQuantity;

    /**
     * 区块中第一笔交易的序列号。
     * 这个序列号是站在整个区块链的角度而产生的，而不是站在这个区块的角度而产生的。
     * 它的值取值：
     * 区块中没有交易，值为0
     * 区块中有交易，值等于 高度低于当前区块的所有区块中包含的交易数量之和+1
     *
     * 冗余字段，这个值可以由区块链计算出来
     */
    private BigInteger startTransactionSequenceNumberInBlockChain;

    /**
     * 区块中最后一笔交易的序列号
     * 这个序列号是站在整个区块链的角度而产生的，而不是站在这个区块的角度而产生的。
     * 它的值取值：
     * 区块中没有交易，值为0
     * 区块中有交易，值等于 高度低于当前区块的所有区块中包含的交易数量之和+当前区块中包含的交易数量
     *
     * 冗余字段，这个值可以由区块链计算出来
     */
    private BigInteger endTransactionSequenceNumberInBlockChain;




    //region get set

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public BigInteger getHeight() {
        return height;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public String getConsensusValue() {
        return consensusValue;
    }

    public void setConsensusValue(String consensusValue) {
        this.consensusValue = consensusValue;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public ConsensusVariableHolder getConsensusVariableHolder() {
        return consensusVariableHolder;
    }

    public void setConsensusVariableHolder(ConsensusVariableHolder consensusVariableHolder) {
        this.consensusVariableHolder = consensusVariableHolder;
    }

    public BigInteger getTransactionQuantity() {
        return transactionQuantity;
    }

    public void setTransactionQuantity(BigInteger transactionQuantity) {
        this.transactionQuantity = transactionQuantity;
    }

    public BigInteger getStartTransactionSequenceNumberInBlockChain() {
        return startTransactionSequenceNumberInBlockChain;
    }

    public void setStartTransactionSequenceNumberInBlockChain(BigInteger startTransactionSequenceNumberInBlockChain) {
        this.startTransactionSequenceNumberInBlockChain = startTransactionSequenceNumberInBlockChain;
    }

    public BigInteger getEndTransactionSequenceNumberInBlockChain() {
        return endTransactionSequenceNumberInBlockChain;
    }

    public void setEndTransactionSequenceNumberInBlockChain(BigInteger endTransactionSequenceNumberInBlockChain) {
        this.endTransactionSequenceNumberInBlockChain = endTransactionSequenceNumberInBlockChain;
    }

    //endregion
}
