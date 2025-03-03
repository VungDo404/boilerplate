package com.app.boilerplate.Service.RateLimiter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Transactional
@Service
public class RateLimiterService {
    private final LettuceBasedProxyManager<byte[]> proxyManager;

    public Bucket resolveBucket(String key, long capacity, long tokens, Duration refillTime) {
        final Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
            .addLimit(limit -> limit.capacity(capacity)
                .refillIntervally(tokens, refillTime))
            .build();

        return proxyManager.builder()
            .build(key.getBytes(), configSupplier);
    }

    public ConsumptionProbe tryConsume(String key, long capacity, long tokens, Duration refillTime) {
        Bucket bucket = resolveBucket(key, capacity,tokens, refillTime);
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    public long getAvailableTokens(String key, long capacity, long tokens, Duration refillTime) {
        Bucket bucket = resolveBucket(key, capacity,tokens, refillTime);
        return bucket.getAvailableTokens();
    }

}