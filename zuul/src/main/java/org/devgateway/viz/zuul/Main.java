package org.devgateway.viz.zuul;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;


import java.util.Collections;

@SpringBootApplication
@EnableDiscoveryClient
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
