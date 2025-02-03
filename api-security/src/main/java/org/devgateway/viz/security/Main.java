package org.devgateway.viz.security;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Collections;

@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Security Service API", version = "v1"), servers = @Server(url = "http://api-security:8763"))
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Bean
    public FilterRegistrationBean forwardedHeaderFilter() {
        final FilterRegistrationBean<ForwardedHeaderFilter> filter = new FilterRegistrationBean<>(new ForwardedHeaderFilter());
        filter.setName("Forwarded Header filter");
        filter.setUrlPatterns(Collections.singletonList("/actuator/*"));
        return filter;
    }
}
