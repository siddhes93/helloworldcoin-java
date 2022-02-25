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
import com.helloworldcoin.util.JsonUtil;
import com.helloworldcoin.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private BlockchainCore blockchainCore;
	private NodeService nodeService;
	private NetCoreConfiguration netCoreConfiguration;

	public NodeServerHandler(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
		super();
		this.blockchainCore = blockchainCore;
		this.nodeService = nodeService;
		this.netCoreConfiguration = netCoreConfiguration;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {

		String requestIp = parseRequestIp(channelHandlerContext);
		String requestApi = parseRequestApi(fullHttpRequest);
		String requestBody = parseRequestBody(fullHttpRequest);

		String responseMessage;
		/*
		 * Any node can access the interface here, please don't write any code here that can expose the user's private key.
		 */
		if("/".equals(requestApi)){
			responseMessage = "Helloworldcoin";
		}else if(API.PING.equals(requestApi)){
			PingRequest request = JsonUtil.toObject(requestBody, PingRequest.class);
			PingResponse response = ping(requestIp,request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.GET_NODES.equals(requestApi)){
			GetNodesRequest request = JsonUtil.toObject(requestBody, GetNodesRequest.class);
			GetNodesResponse getBlockResponse = getNodes(request);
			responseMessage = JsonUtil.toString(getBlockResponse);
		}else if(API.GET_BLOCK.equals(requestApi)){
			GetBlockRequest request = JsonUtil.toObject(requestBody, GetBlockRequest.class);
			GetBlockResponse getBlockResponse = getBlock(request);
			responseMessage = JsonUtil.toString(getBlockResponse);
		}else if(API.GET_UNCONFIRMED_TRANSACTIONS.equals(requestApi)){
			GetUnconfirmedTransactionsRequest request = JsonUtil.toObject(requestBody, GetUnconfirmedTransactionsRequest.class);
			GetUnconfirmedTransactionsResponse response = getUnconfirmedTransactions(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_TRANSACTION.equals(requestApi)){
			PostTransactionRequest request = JsonUtil.toObject(requestBody, PostTransactionRequest.class);
			PostTransactionResponse response = postTransaction(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_BLOCK.equals(requestApi)){
			PostBlockRequest request = JsonUtil.toObject(requestBody, PostBlockRequest.class);
			PostBlockResponse response = postBlock(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			PostBlockchainHeightRequest request = JsonUtil.toObject(requestBody, PostBlockchainHeightRequest.class);
			PostBlockchainHeightResponse response = postBlockchainHeight(requestIp,request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.GET_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			GetBlockchainHeightRequest request = JsonUtil.toObject(requestBody, GetBlockchainHeightRequest.class);
			GetBlockchainHeightResponse response = getBlockchainHeight(request);
			responseMessage = JsonUtil.toString(response);
		}else {
			responseMessage = "404 page not found";
		}
		writeResponse(channelHandlerContext, responseMessage);
	}

	private String parseRequestBody(FullHttpRequest fullHttpRequest) {
		return fullHttpRequest.content().toString(CharsetUtil.UTF_8);
	}

	private String parseRequestIp(ChannelHandlerContext channelHandlerContext) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
		String ip = inetSocketAddress.getAddress().getHostAddress();
		return ip;
	}

	private String parseRequestApi(FullHttpRequest fullHttpRequest) {
		String uri = fullHttpRequest.uri();
		if(uri.contains("?")){
			return uri.split("\\?")[0];
		}else {
			return uri;
		}
	}

	private void writeResponse(ChannelHandlerContext channelHandlerContext, String msg) {
		ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bf);
		res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length());
		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		channelHandlerContext.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
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