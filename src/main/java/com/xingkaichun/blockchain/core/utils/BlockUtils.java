package com.xingkaichun.blockchain.core.utils;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.utils.atomic.CipherUtil;

public class BlockUtils {

    public static String calculateHash(Block block) {

        return CipherUtil.applySha256(block.getPreviousHash() + block.getNonce() + block.getMerkleRoot());
    }

    public static boolean checkHash(Block block) {
        String hash = calculateHash(block);
        return block.getHash().equals(hash);
    }
}
