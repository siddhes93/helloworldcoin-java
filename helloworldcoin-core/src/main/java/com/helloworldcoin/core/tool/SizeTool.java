package com.helloworldcoin.core.tool;

import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.model.transaction.Transaction;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class SizeTool {

    //region check Size
    /**
     * check Block Size: used to limit the size of the block.
     */
    public static boolean checkBlockSize(Block block) {
        return DtoSizeTool.checkBlockSize(Model2DtoTool.block2BlockDto(block));
    }
    /**
     * Check transaction size: used to limit the size of the transaction.
     */
    public static boolean checkTransactionSize(Transaction transaction) {
        return DtoSizeTool.checkTransactionSize(Model2DtoTool.transaction2TransactionDto(transaction));
    }
    //endregion



    //region calculate Size
    public static long calculateBlockSize(Block block) {
        return DtoSizeTool.calculateBlockSize(Model2DtoTool.block2BlockDto(block));
    }
    public static long calculateTransactionSize(Transaction transaction) {
        return DtoSizeTool.calculateTransactionSize(Model2DtoTool.transaction2TransactionDto(transaction));
    }
    //endregion
}
