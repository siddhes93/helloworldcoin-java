package com.helloworldcoin.netcore.server;

import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	private BlockchainCore blockchainCore;
	private NodeService nodeService;
	private NetCoreConfiguration netCoreConfiguration;

	public NodeServerChannelInitializer(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
		super();
		this.blockchainCore = blockchainCore;
		this.nodeService = nodeService;
		this.netCoreConfiguration = netCoreConfiguration;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) {
		ch.pipeline().addLast("codec", new HttpServerCodec());
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(10*1024*1024));
		ch.pipeline().addLast("serverHandler", new NodeServerHandler(netCoreConfiguration,blockchainCore,nodeService));
	}

}
