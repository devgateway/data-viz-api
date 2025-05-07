package org.devgateway.viz.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = true)

@EnableCaching

public class SupersetProxyApplication {
    //TODO:add logger

    public static void main(String[] args) {
        SpringApplication.run(SupersetProxyApplication.class, args);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // return new NoOpCacheManager();

        return RedisCacheManager.builder(redisConnectionFactory).build();
    }

    @Bean
    public KeyGenerator customKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                return target.getClass().getSimpleName() + "_"
                        + method.getName() + "_"
                        + Arrays.stream(params).map(Object::toString).collect(Collectors.joining("_"));
            }
        };

    }

}