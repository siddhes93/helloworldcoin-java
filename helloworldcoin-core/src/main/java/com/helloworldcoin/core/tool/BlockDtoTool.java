package com.helloworldcoin.core.tool;

import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.crypto.MerkleTreeUtil;
import com.helloworldcoin.crypto.Sha256Util;
import com.helloworldcoin.netcore.dto.BlockDto;
import com.helloworldcoin.netcore.dto.TransactionDto;
import com.helloworldcoin.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockDtoTool {

    /**
     * calculate Block Hash
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
     * calculate Block Merkle Tree Root
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
     * is Block Equals ?
     */
    public static boolean isBlockEquals(BlockDto block1, BlockDto block2) {
        String block1Hash = calculateBlockHash(block1);
        String block2Hash = calculateBlockHash(block2);
        return StringUtil.equals(block1Hash, block2Hash);
    }
}
