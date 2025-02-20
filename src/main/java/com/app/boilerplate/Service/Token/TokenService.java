package com.app.boilerplate.Service.Token;

import com.app.boilerplate.Config.TokenAuthConfig;
import com.app.boilerplate.Domain.User.Token;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Repository.TokenRepository;
import com.app.boilerplate.Shared.Authentication.TokenType;
import com.app.boilerplate.Util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional
@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final RandomUtil randomUtil;
    private final TokenAuthConfig tokenAuthConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    private Token addToken(TokenType type, String value, LocalDateTime expireDate, User user) {
        final var token = Token.builder()
                .type(type)
                .value(value)
                .expireDate(expireDate)
                .user(user)
                .build();
        return tokenRepository.save(token);
    }

    @CachePut(value = "token", key = "#result.value")
    public Token addToken(TokenType type, User user) {
        final var key = randomUtil.randomToken();
        final var exp = LocalDateTime.now().plusDays(1);
        return addToken(type, key, exp, user);
    }

    public void addRefreshToken(User user, String value){
        final var expireDate = LocalDateTime.now().plus(tokenAuthConfig.getRefreshTokenExpirationInSeconds());
        addToken(TokenType.REFRESH_TOKEN, value, expireDate, user);
    }

    public void addAccessToken(User user, String value){
        final var expireDate = LocalDateTime.now().plus(tokenAuthConfig.getAccessTokenExpirationInSeconds());
        addToken(TokenType.ACCESS_TOKEN, value, expireDate, user);
        final var ttl = tokenAuthConfig.getAccessTokenExpirationInSeconds().dividedBy(2);
        redisTemplate.opsForValue().set("access_token::"+value, "", ttl.toSeconds(), TimeUnit.SECONDS);
    }

    public boolean checkIfJwtExists(String value){
        boolean retrievedValue = Boolean.TRUE.equals(redisTemplate.hasKey("access_token::" + value));
        if (retrievedValue) return true;
        return tokenRepository.existsByValue(value);
    }



    public void deleteExpiredTokens(LocalDateTime dateTime) {
        tokenRepository.deleteByExpireDateBefore(dateTime);
    }

    @Scheduled(cron = "${cleanup.cron.token}")
    public void deleteExpiredTokens() {
        deleteExpiredTokens(LocalDateTime.now());
    }

    @Cacheable(value = "token", key = "#value")
    public Token getTokenByTypeAndValue(TokenType type, String value) {
        return tokenRepository.findByTypeAndValue(type, value)
                .filter(token -> !LocalDateTime.now()
                        .isAfter(token.getExpireDate()))
                .orElse(null);
    }

    @Async
    @CacheEvict(value = "token", key = "#value")
    public void deleteByValue(String value) {
        tokenRepository.deleteByValue(value);
    }

    public void deleteByTypeAndUser(TokenType type, User user) {
        final var tokens = findByTypeAndUser(type, user);
        for (Token token : tokens) {
            Objects.requireNonNull(cacheManager.getCache("token"))
                    .evict(token.getValue());
        }
        tokenRepository.deleteTokenByTypeAndUser(type, user);
    }

    private List<Token> findByTypeAndUser(TokenType type, User user) {
        return tokenRepository.findAllByTypeAndUser(type, user);
    }
}
