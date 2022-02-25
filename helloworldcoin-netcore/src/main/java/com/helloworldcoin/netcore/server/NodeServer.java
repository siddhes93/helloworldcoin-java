package com.helloworldcoin.netcore.server;

import com.helloworldcoin.netcore.configuration.NetCoreConfiguration;
import com.helloworldcoin.netcore.service.NodeService;
import com.helloworldcoin.core.BlockchainCore;
import com.helloworldcoin.setting.NetworkSetting;
import com.helloworldcoin.util.SystemUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * node server
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeServer {

	private BlockchainCore blockchainCore;
	private NodeService nodeService;
	private NetCoreConfiguration netCoreConfiguration;

	public NodeServer(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
		super();
		this.blockchainCore = blockchainCore;
		this.nodeService = nodeService;
		this.netCoreConfiguration = netCoreConfiguration;
	}


	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new NodeServerChannelInitializer(netCoreConfiguration,blockchainCore,nodeService))
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(NetworkSetting.PORT).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			SystemUtil.errorExit("blockchain node server can not start.",e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}