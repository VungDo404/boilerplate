package com.app.boilerplate.Service.Token;

import com.app.boilerplate.Domain.User.Token;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Repository.TokenRepository;
import com.app.boilerplate.Shared.Authentication.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    @Async
    public void addToken(TokenType type, String value, LocalDateTime expireDate, User user) {
        final var token = Token.builder()
                .type(type)
                .value(value)
                .expireDate(expireDate)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    public void deleteExpiredTokens(LocalDateTime dateTime) {
        tokenRepository.deleteByExpireDateBefore(dateTime);
    }

    @Scheduled(cron = "${cleanup.cron.token}")
    public void deleteExpiredTokens() {
        deleteExpiredTokens(LocalDateTime.now());
    }

    public Token getTokenByTypeAndValue(TokenType type, String value) {
        return tokenRepository.findByTypeAndValue(type, value)
                .filter(token -> !LocalDateTime.now()
                        .isAfter(token.getExpireDate()))
                .orElse(null);
    }

    @Async
    public void deleteByValue(String value) {
        tokenRepository.deleteByValue(value);
    }

    @Async
    public void deleteByTypeAndUser(TokenType type, User user) {
        tokenRepository.deleteTokenByTypeAndUser(type, user);
    }
}
