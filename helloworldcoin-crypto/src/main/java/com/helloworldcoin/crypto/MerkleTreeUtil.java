package com.helloworldcoin.crypto;

import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.MathUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Merkle Tree Util
 *
 * @author x.king xdotking@gmail.com
 */
public class MerkleTreeUtil {

    /**
     * calculate merkle tree root
     *
     * @author x.king xdotking@gmail.com
     */
    public static byte[] calculateMerkleTreeRoot(List<byte[]> datas) {
        List<byte[]> tree = new ArrayList<>(datas);
        int size = tree.size();
        int levelOffset = 0;
        for (int levelSize = size; levelSize > 1; levelSize = (levelSize + 1) / 2) {
            for (int left = 0; left < levelSize; left += 2) {
                int right = MathUtil.min(left + 1, levelSize - 1);
                byte[] leftBytes = tree.get(levelOffset + left);
                byte[] rightBytes = tree.get(levelOffset + right);
                tree.add(Sha256Util.doubleDigest(ByteUtil.concatenate(leftBytes, rightBytes)));
            }
            levelOffset += levelSize;
        }
        return tree.get(tree.size()-1);
    }
}