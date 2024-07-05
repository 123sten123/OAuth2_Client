package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class ClientApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);

	@Autowired
	private ExampleClient exampleClient;
//

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Exchanges authenticated using WebClient.Builder with OAuth2
		//@formatter:off
		try {
			exampleClient.getInfo();
		}catch (Exception e){
			LOGGER.error(e.getMessage());
		}
//		System.out.println(s);
		//@formatter:on
	}
}