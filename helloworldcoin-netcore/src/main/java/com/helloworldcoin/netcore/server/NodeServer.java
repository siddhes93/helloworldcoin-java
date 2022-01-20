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
 * 区块链节点服务器：其它节点与之通信，同步节点数据、区块数据、交易数据等。
 *
 * @author x.king xdotking@gmail.com
 */
public class NodeServer {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public NodeServer(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
		super();
		this.httpServerHandlerResolver = new HttpServerHandlerResolver(netCoreConfiguration,blockchainCore,nodeService);
	}


	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new HttpServerChannelInitializer(httpServerHandlerResolver))
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