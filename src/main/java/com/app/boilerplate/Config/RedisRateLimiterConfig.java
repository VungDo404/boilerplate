package com.app.boilerplate.Config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RedisRateLimiterConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database}")
    private int redisDatabase;

    @Bean
    public RedisClient redisClient() {
        RedisURI redisUri = RedisURI.builder()
            .withHost(redisHost)
            .withPort(redisPort)
            .withDatabase(redisDatabase)
            .withAuthentication("default", redisPassword.toCharArray())
            .build();
        return RedisClient.create(redisUri);
    }

    @Bean
    public LettuceBasedProxyManager<byte[]> redisProxyManager(RedisClient redisClient) {
        final var clientSideConfig = ClientSideConfig.getDefault()
            .withExpirationAfterWriteStrategy(ExpirationAfterWriteStrategy
                .basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1)));

        return LettuceBasedProxyManager.builderFor(redisClient)
            .withClientSideConfig(clientSideConfig)
            .build();
    }

}
