package com.app.boilerplate.Service.Authentication;

import com.app.boilerplate.Exception.InvalidVerificationCodeException;
import com.app.boilerplate.Service.Token.TokenService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Event.TwoFactorCodeEvent;
import com.app.boilerplate.Shared.Account.Model.TOTPModel;
import com.app.boilerplate.Shared.Authentication.Dto.SendTwoFactorCodeDto;
import com.app.boilerplate.Shared.Authentication.TwoFactorProvider;
import com.app.boilerplate.Util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TwoFactorService {
    private final UserService userService;
    private final TwoFactorCacheService twoFactorCacheService;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenService tokenService;

    @Value("${spring.application.name}")
    private String APP_NAME;

    public void sendTwoFactorCode(SendTwoFactorCodeDto dto) {
        final var user = userService.getUserById(dto.getUserId());
        final var provider = twoFactorCacheService.addTwoFactorProvider(dto.getProvider(), user.getId()
            .toString());
        if (!provider.equals(TwoFactorProvider.GOOGLE_AUTHENTICATOR)) {
            final var code = twoFactorCacheService.addTwoFactorCode(dto.getUserId()
                .toString());
            if (provider
                .equals(TwoFactorProvider.EMAIL)) {
                eventPublisher.publishEvent(new TwoFactorCodeEvent(user, code));
            }
        }
    }

    public void validateTwoFactorCode(UUID userId, String submitCode) {
        final var id = userId.toString();
        final var provider = twoFactorCacheService.getTwoFactorProvider(id);
        if(provider.equals(TwoFactorProvider.GOOGLE_AUTHENTICATOR)) {
            final var user = userService.getUserById(userId);
            final var token = tokenService.getAuthenticatorToken(user);
            if(!validateCode(token.getValue(), submitCode))
                throw new InvalidVerificationCodeException("error.auth.two-factor");
        }else{
            final var code = twoFactorCacheService.getTwoFactorCode(id);
            if (!submitCode.equals(code)) {
                throw new InvalidVerificationCodeException("error.auth.two-factor");
            }
            twoFactorCacheService.deleteTwoFactorCode(id);
        }
        twoFactorCacheService.deleteTwoFactorProvider(id);
    }

    public boolean validateCode(String secretKey, String inputCode) {
        final var WINDOW_SIZE = 1;
        try {
            long timeWindow = System.currentTimeMillis() / 30000;

            for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
                final var hash = RandomUtil.generateTOTP(secretKey, timeWindow + i);
                if (Objects.equals(hash, inputCode)) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public TOTPModel generateSecret(String name){
        final var secret = RandomUtil.generateSecretKey();
        final var uri = getQRCodeURI(name, APP_NAME, secret);
        return TOTPModel.builder()
            .secret(secret)
            .uri(uri)
            .build();
    }

    public TOTPModel getUri(String secret){
        final var uri = getQRCodeURI(secret, APP_NAME, secret);
        return TOTPModel.builder()
            .uri(uri)
            .secret(secret)
            .build();
    }

    private String getQRCodeURI(String accountName, String issuer, String secretKey) {
        try {
            return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=30",
                URLEncoder.encode(issuer, StandardCharsets.UTF_8),
                URLEncoder.encode(accountName, StandardCharsets.UTF_8),
                secretKey,
                URLEncoder.encode(issuer, StandardCharsets.UTF_8),
                RandomUtil.CODE_DIGITS
                                );
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code URI", e);
        }
    }
}