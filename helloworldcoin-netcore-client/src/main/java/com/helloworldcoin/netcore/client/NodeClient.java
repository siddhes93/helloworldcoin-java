package com.helloworldcoin.netcore.client;

import com.helloworldcoin.netcore.dto.*;

/**
 * node client
 *
 * @author x.king xdotking@gmail.com
 */
public interface NodeClient {

    /**
     * post transaction to node
     */
    PostTransactionResponse postTransaction(PostTransactionRequest request);

    /**
     * ping node
     */
    PingResponse pingNode(PingRequest request);

    /**
     * get block by block height
     */
    GetBlockResponse getBlock(GetBlockRequest request);

    /**
     * get nodes
     */
    GetNodesResponse getNodes(GetNodesRequest request);

    /**
     * post block to node
     */
    PostBlockResponse postBlock(PostBlockRequest request);

    /**
     * post blockchain height to node
     */
    PostBlockchainHeightResponse postBlockchainHeight(PostBlockchainHeightRequest request);

    /**
     * get blockchain height
     */
    GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request);

    /**
     * get unconfirmed transactions
     */
    GetUnconfirmedTransactionsResponse getUnconfirmedTransactions(GetUnconfirmedTransactionsRequest getUnconfirmedTransactionsRequest);
}
