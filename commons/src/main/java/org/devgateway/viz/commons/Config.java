package org.devgateway.viz.commons;

import org.devgateway.viz.commons.observers.DatasetListener;
import org.devgateway.viz.commons.services.generic.utils.StatsKeyGeneration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.concurrent.Executor;

@Configuration()
@EnableTransactionManagement
//@EnableSwagger2
@EntityScan(basePackages = "org.devgateway.viz.commons.domain")
@EnableJpaRepositories(basePackages = "org.devgateway.viz.commons.repositories")
@ComponentScan(basePackages = "org.devgateway.viz")
@PropertySource("classpath:common.properties")
@EnableDiscoveryClient(autoRegister = true)

@EnableCaching
@EnableAsync

public class Config extends CachingConfigurerSupport implements WebMvcConfigurer {

    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    @Bean("statsKeyGeneration")
    public StatsKeyGeneration keyGenerator() {
        return new StatsKeyGeneration();
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        // configure and return an implementation of Spring's CacheManager SPI
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
                Arrays.asList(
                        new ConcurrentMapCache("stats"),
                        new ConcurrentMapCache("categories"),
                        new ConcurrentMapCache("utils")

                ));
        return cacheManager;
    }

    @Bean(name = "asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public DatasetListener datasetListener() {
        return new DatasetListener();
    }



}
