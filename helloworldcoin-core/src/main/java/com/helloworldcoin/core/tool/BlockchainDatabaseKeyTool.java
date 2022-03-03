package com.helloworldcoin.core.tool;

import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.StringUtil;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class BlockchainDatabaseKeyTool {

    //BLOCKCHAIN HEIGHT KEY => blockchain height value
    private static final String BLOCKCHAIN_HEIGHT_KEY = "A";
    //BLOCKCHAIN TRANSACTION HEIGHT KEY => blockchain transaction height value
    private static final String BLOCKCHAIN_TRANSACTION_HEIGHT_KEY = "B";
    //BLOCKCHAIN TRANSACTION OUTPUT HEIGHT KEY => blockchain transaction output height value
    private static final String BLOCKCHAIN_TRANSACTION_OUTPUT_HEIGHT_KEY = "C";

    //HASH FLAG: hash prefix flag
    private static final String HASH_PREFIX_FLAG = "D";


    //BLOCK FLAG: block height => block
    private static final String BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG = "E";
    //BLOCK FLAG: block hash => block
    private static final String BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG = "F";


    //TRANSACTION FLAG: transaction height => transaction
    private static final String TRANSACTION_HEIGHT_TO_TRANSACTION_PREFIX_FLAG = "G";
    //TRANSACTION FLAG: transaction hash => transaction height
    private static final String TRANSACTION_HASH_TO_TRANSACTION_HEIGHT_PREFIX_FLAG = "H";


    //TRANSACTION OUTPUT FLAG: transaction output height => transaction output
    private static final String TRANSACTION_OUTPUT_HEIGHT_TO_TRANSACTION_OUTPUT_PREFIX_FLAG = "I";
    //TRANSACTION OUTPUT FLAG: transaction output id => transaction output height
    private static final String TRANSACTION_OUTPUT_ID_TO_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG = "J";
    //TRANSACTION OUTPUT FLAG: transaction output id => unspent transaction output height
    private static final String TRANSACTION_OUTPUT_ID_TO_UNSPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG = "K";
    //TRANSACTION OUTPUT FLAG: transaction output id => spent transaction output height
    private static final String TRANSACTION_OUTPUT_ID_TO_SPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG = "L";
    //TRANSACTION OUTPUT FLAG: transaction output id => source transaction output height
    private static final String TRANSACTION_OUTPUT_ID_TO_SOURCE_TRANSACTION_HEIGHT_PREFIX_FLAG = "M";
    //TRANSACTION OUTPUT FLAG: transaction output id => to transaction output height
    private static final String TRANSACTION_OUTPUT_ID_TO_DESTINATION_TRANSACTION_HEIGHT_PREFIX_FLAG = "N";


    //ADDRESS FLAG: address prefix flag
    private static final String ADDRESS_PREFIX_FLAG = "O";
    //ADDRESS FLAG: address => transaction output height
    private static final String ADDRESS_TO_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG = "P";
    //ADDRESS FLAG: address => unspent transaction output height
    private static final String ADDRESS_TO_UNSPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG = "Q";
    //ADDRESS FLAG: address => spent transaction output height
    private static final String ADDRESS_TO_SPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG = "R";


    private static final String END_FLAG = "#" ;
    private static final String VERTICAL_LINE_FLAG = "|" ;




    //region Build Key
    public static byte[] buildBlockchainHeightKey() {
        String stringKey = BLOCKCHAIN_HEIGHT_KEY + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildHashKey(String hash) {
        String stringKey = HASH_PREFIX_FLAG + hash + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildAddressKey(String address) {
        String stringKey = ADDRESS_PREFIX_FLAG + address + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildBlockHeightToBlockKey(long blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG + StringUtil.valueOfUint64(blockHeight) + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildBlockHashToBlockHeightKey(String blockHash) {
        String stringKey = BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG + blockHash + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionHashToTransactionHeightKey(String transactionHash) {
        String stringKey = TRANSACTION_HASH_TO_TRANSACTION_HEIGHT_PREFIX_FLAG + transactionHash + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionOutputHeightToTransactionOutputKey(long transactionOutputHeight) {
        String stringKey = TRANSACTION_OUTPUT_HEIGHT_TO_TRANSACTION_OUTPUT_PREFIX_FLAG + StringUtil.valueOfUint64(transactionOutputHeight) + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionOutputIdToTransactionOutputHeightKey(String transactionHash,long transactionOutputIndex) {
        String transactionOutputId = buildTransactionOutputId(transactionHash, transactionOutputIndex);
        String stringKey = TRANSACTION_OUTPUT_ID_TO_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG + transactionOutputId + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionOutputIdToUnspentTransactionOutputHeightKey(String transactionHash,long transactionOutputIndex) {
        String transactionOutputId = buildTransactionOutputId(transactionHash, transactionOutputIndex);
        String stringKey = TRANSACTION_OUTPUT_ID_TO_UNSPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG + transactionOutputId + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionOutputIdToSourceTransactionHeightKey(String transactionHash,long transactionOutputIndex) {
        String transactionOutputId = buildTransactionOutputId(transactionHash, transactionOutputIndex);
        String stringKey = TRANSACTION_OUTPUT_ID_TO_SOURCE_TRANSACTION_HEIGHT_PREFIX_FLAG + transactionOutputId + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionOutputIdToDestinationTransactionHeightKey(String transactionHash,long transactionOutputIndex) {
        String transactionOutputId = buildTransactionOutputId(transactionHash, transactionOutputIndex);
        String stringKey = TRANSACTION_OUTPUT_ID_TO_DESTINATION_TRANSACTION_HEIGHT_PREFIX_FLAG + transactionOutputId + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOutputHeightKey(String address) {
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG + address + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildAddressToUnspentTransactionOutputHeightKey(String address) {
        String stringKey = ADDRESS_TO_UNSPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG + address + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildAddressToSpentTransactionOutputHeightKey(String address) {
        String stringKey = ADDRESS_TO_SPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG + address + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildBlockchainTransactionHeightKey() {
        String stringKey = BLOCKCHAIN_TRANSACTION_HEIGHT_KEY + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildBlockchainTransactionOutputHeightKey() {
        String stringKey = BLOCKCHAIN_TRANSACTION_OUTPUT_HEIGHT_KEY + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionHeightToTransactionKey(long transactionHeight) {
        String stringKey = TRANSACTION_HEIGHT_TO_TRANSACTION_PREFIX_FLAG + transactionHeight + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionOutputIdToSpentTransactionOutputHeightKey(String transactionHash,long transactionOutputIndex) {
        String transactionOutputId = buildTransactionOutputId(transactionHash, transactionOutputIndex);
        String stringKey = TRANSACTION_OUTPUT_ID_TO_SPENT_TRANSACTION_OUTPUT_HEIGHT_PREFIX_FLAG + transactionOutputId + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    //endregion

    public static String buildTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        String transactionOutputId = StringUtil.concatenate3(transactionHash, VERTICAL_LINE_FLAG, StringUtil.valueOfUint64(transactionOutputIndex));
        return transactionOutputId;
    }
}