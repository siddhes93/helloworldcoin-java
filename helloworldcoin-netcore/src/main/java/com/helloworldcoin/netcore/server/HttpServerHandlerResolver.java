package com.helloworldcoin.netcore.server;

import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.core.UnconfirmedTransactionDatabase;
import com.helloworldcoin.core.model.Block;
import com.helloworldcoin.core.tool.Model2DtoTool;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.netcore.model.Node;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.setting.BlockSetting;
import com.helloworldcoin.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO rename
 * @author x.king xdotking@gmail.com
 */
public class HttpServerHandlerResolver {

    private BlockchainCore blockchainCore;
    private NodeService nodeService;
    private NetCoreConfiguration netCoreConfiguration;

    public HttpServerHandlerResolver(NetCoreConfiguration netCoreConfiguration,BlockchainCore blockchainCore, NodeService nodeService) {
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
        this.netCoreConfiguration = netCoreConfiguration;
    }

    public PingResponse ping(String requestIp, PingRequest request){
        try {
            if(netCoreConfiguration.isAutoSearchNode()){
                Node node = new Node();
                node.setIp(requestIp);
                node.setBlockchainHeight(0);
                nodeService.addNode(node);
            }
            PingResponse response = new PingResponse();
            return response;
        } catch (Exception e){
            String message = "ping node failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetBlockResponse getBlock(GetBlockRequest request){
        try {
            Block blockByBlockHeight = blockchainCore.queryBlockByBlockHeight(request.getBlockHeight());
            BlockDto block = Model2DtoTool.block2BlockDto(blockByBlockHeight);
            GetBlockResponse response = new GetBlockResponse();
            response.setBlock(block);
            return response;
        } catch (Exception e){
            String message = "get block failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public PostTransactionResponse postTransaction(PostTransactionRequest request){
        try {
            blockchainCore.postTransaction(request.getTransaction());
            PostTransactionResponse response = new PostTransactionResponse();
            return response;
        } catch (Exception e){
            String message = "post transaction failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public PostBlockResponse postBlock(PostBlockRequest request) {
        try {
            blockchainCore.addBlockDto(request.getBlock());
            PostBlockResponse response = new PostBlockResponse();
            return response;
        } catch (Exception e){
            String message = "post block failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetNodesResponse getNodes(GetNodesRequest request) {
        try {
            List<Node> allNodes = nodeService.queryAllNodes();
            List<NodeDto> nodes = new ArrayList<>();
            if(allNodes != null){
                for (Node node:allNodes) {
                    NodeDto n = new NodeDto();
                    n.setIp(node.getIp());
                    nodes.add(n);
                }
            }
            GetNodesResponse response = new GetNodesResponse();
            response.setNodes(nodes);
            return response;
        }catch (Exception e){
            String message = "get nodes failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public PostBlockchainHeightResponse postBlockchainHeight(String requestIp, PostBlockchainHeightRequest request) {
        try {
            Node node = new Node();
            node.setIp(requestIp);
            node.setBlockchainHeight(request.getBlockchainHeight());
            nodeService.updateNode(node);
            PostBlockchainHeightResponse response = new PostBlockchainHeightResponse();
            return response;
        } catch (Exception e){
            String message = "post blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request) {
        try {
            long blockchainHeight = blockchainCore.queryBlockchainHeight();
            GetBlockchainHeightResponse response = new GetBlockchainHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return response;
        } catch (Exception e){
            String message = "get blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetUnconfirmedTransactionsResponse getUnconfirmedTransactions(GetUnconfirmedTransactionsRequest request) {
        try {
            UnconfirmedTransactionDatabase unconfirmedTransactionDatabase = blockchainCore.getUnconfirmedTransactionDatabase();
            List<TransactionDto> transactions = unconfirmedTransactionDatabase.selectTransactions(1, BlockSetting.BLOCK_MAX_TRANSACTION_COUNT);
            GetUnconfirmedTransactionsResponse response = new GetUnconfirmedTransactionsResponse();
            response.setTransactions(transactions);
            return response;
        } catch (Exception e){
            String message = "get transaction failed";
            LogUtil.error(message,e);
            return null;
        }
    }
}