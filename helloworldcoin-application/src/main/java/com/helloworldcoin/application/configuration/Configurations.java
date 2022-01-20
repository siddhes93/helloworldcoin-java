package com.helloworldcoin.application.configuration;

import com.helloworldcoin.core.tool.ResourcePathTool;
import com.helloworldcoin.netcore.BlockchainNetCore;
import com.helloworldcoin.netcore.BlockchainNetCoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 * @author x.king xdotking@gmail.com
 */
@Configuration
public class Configurations {

	@Bean
	public BlockchainNetCore blockchainNetCore() {
		BlockchainNetCore blockchainNetCore = BlockchainNetCoreFactory.createBlockchainNetCore(ResourcePathTool.getDataRootPath());
		blockchainNetCore.start();
		return blockchainNetCore;
	}
}