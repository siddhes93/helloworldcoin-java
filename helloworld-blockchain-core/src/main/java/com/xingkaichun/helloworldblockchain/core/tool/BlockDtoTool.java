package com.xingkaichun.helloworldblockchain.core.tool;

import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.MerkleTreeUtil;
import com.xingkaichun.helloworldblockchain.crypto.Sha256Util;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockDtoTool {

    /**
     * 计算区块的Hash值
     */
    public static String calculateBlockHash(BlockDto blockDto) {
        byte[] bytesTimestamp = ByteUtil.uint64ToBytes(blockDto.getTimestamp());
        byte[] bytesPreviousBlockHash = ByteUtil.hexStringToBytes(blockDto.getPreviousHash());
        byte[] bytesMerkleTreeRoot = ByteUtil.hexStringToBytes(calculateBlockMerkleTreeRoot(blockDto));
        byte[] bytesNonce = ByteUtil.hexStringToBytes(blockDto.getNonce());

        byte[] bytesBlockHeader = ByteUtil.concatenate4(bytesTimestamp,bytesPreviousBlockHash,bytesMerkleTreeRoot,bytesNonce);
        byte[] bytesBlockHash = Sha256Util.doubleDigest(bytesBlockHeader);
        return ByteUtil.bytesToHexString(bytesBlockHash);
    }

    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(BlockDto blockDto) {
        List<TransactionDto> transactions = blockDto.getTransactions();
        List<byte[]> bytesTransactionHashs = new ArrayList<>();
        if(transactions != null){
            for(TransactionDto transactionDto : transactions) {
                String transactionHash = TransactionDtoTool.calculateTransactionHash(transactionDto);
                byte[] bytesTransactionHash = ByteUtil.hexStringToBytes(transactionHash);
                bytesTransactionHashs.add(bytesTransactionHash);
            }
        }
        return ByteUtil.bytesToHexString(MerkleTreeUtil.calculateMerkleTreeRoot(bytesTransactionHashs));
    }

    /**
     * 简单的校验两个区块是否相等
     * 注意：这里没有严格校验,例如没有校验区块中的交易是否完全一样
     * ，所以即使这里认为两个区块相等，实际上这两个区块还是有可能不相等的。
     */
    public static boolean isBlockEquals(BlockDto block1, BlockDto block2) {
        String block1Hash = calculateBlockHash(block1);
        String block2Hash = calculateBlockHash(block2);
        return StringUtil.equals(block1Hash, block2Hash);
    }
}
