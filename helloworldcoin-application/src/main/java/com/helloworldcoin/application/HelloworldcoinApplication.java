package com.helloworldcoin.application;

import com.helloworldcoin.util.SystemUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
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
