package org.devgateway.viz.zuul;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Collections;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Zuul Service API", version = "v1"), servers = @Server(url = "http://zuul:8762"))
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
