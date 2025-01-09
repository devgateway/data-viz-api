package org.devgateway.viz.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SuperSetProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(SuperSetProxyApplication.class, args);
	}

}