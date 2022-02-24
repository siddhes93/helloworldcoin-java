package com.helloworldcoin.netcore.server;

import com.helloworldcoin.netcore.dto.*;
import com.helloworldcoin.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public HttpServerHandler(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
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
			PingResponse response = httpServerHandlerResolver.ping(requestIp,request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.GET_NODES.equals(requestApi)){
			GetNodesRequest request = JsonUtil.toObject(requestBody, GetNodesRequest.class);
			GetNodesResponse getBlockResponse = httpServerHandlerResolver.getNodes(request);
			responseMessage = JsonUtil.toString(getBlockResponse);
		}else if(API.GET_BLOCK.equals(requestApi)){
			GetBlockRequest request = JsonUtil.toObject(requestBody, GetBlockRequest.class);
			GetBlockResponse getBlockResponse = httpServerHandlerResolver.getBlock(request);
			responseMessage = JsonUtil.toString(getBlockResponse);
		}else if(API.GET_UNCONFIRMED_TRANSACTIONS.equals(requestApi)){
			GetUnconfirmedTransactionsRequest request = JsonUtil.toObject(requestBody, GetUnconfirmedTransactionsRequest.class);
			GetUnconfirmedTransactionsResponse response = httpServerHandlerResolver.getUnconfirmedTransactions(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_TRANSACTION.equals(requestApi)){
			PostTransactionRequest request = JsonUtil.toObject(requestBody, PostTransactionRequest.class);
			PostTransactionResponse response = httpServerHandlerResolver.postTransaction(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_BLOCK.equals(requestApi)){
			PostBlockRequest request = JsonUtil.toObject(requestBody, PostBlockRequest.class);
			PostBlockResponse response = httpServerHandlerResolver.postBlock(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			PostBlockchainHeightRequest request = JsonUtil.toObject(requestBody, PostBlockchainHeightRequest.class);
			PostBlockchainHeightResponse response = httpServerHandlerResolver.postBlockchainHeight(requestIp,request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.GET_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			GetBlockchainHeightRequest request = JsonUtil.toObject(requestBody, GetBlockchainHeightRequest.class);
			GetBlockchainHeightResponse response = httpServerHandlerResolver.getBlockchainHeight(request);
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
}