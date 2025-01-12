package com.app.boilerplate.Config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
public class RedisConfig implements CachingConfigurer {
	@Bean
	RedisConnectionFactory redisConnectionFactory(RedisProperties props) {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setPassword(props.getPassword());
		config.setHostName(props.getHost());
		config.setPort(props.getPort());
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public CacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
		final var cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofHours(1));
		return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(cacheConfig)
			.build();
	}


}
