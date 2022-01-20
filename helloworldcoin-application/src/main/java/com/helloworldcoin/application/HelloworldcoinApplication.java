package com.helloworldcoin.application;

import com.helloworldcoin.util.SystemUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 区块链应用层：包含钱包应用、区块链浏览器应用、节点控制台应用。
 *
 * @author x.king xdotking@gmail.com
 */
@SpringBootApplication
public class HelloworldcoinApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloworldcoinApplication.class, args);
		SystemUtil.callDefaultBrowser("http://localhost/");
	}
}
