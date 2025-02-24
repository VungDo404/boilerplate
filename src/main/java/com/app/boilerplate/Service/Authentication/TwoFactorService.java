package com.app.boilerplate.Service.Authentication;

import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Event.TwoFactorCodeEvent;
import com.app.boilerplate.Shared.Authentication.TwoFactorProvider;
import com.app.boilerplate.Shared.Authentication.Dto.SendTwoFactorCodeDto;
import com.app.boilerplate.Shared.Authentication.Model.SendTwoFactorCodeModel;
import com.app.boilerplate.Util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
@Transactional
@Service
public class TwoFactorService {
    private final UserService userService;
    private final RandomUtil randomUtil;
    private final TwoFactorCacheService twoFactorCacheService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${spring.application.name}")
    private String APP_NAME;

    public SendTwoFactorCodeModel sendTwoFactorCode(SendTwoFactorCodeDto dto) {
        final var user = userService.getUserById(dto.getUserId());
        final var provider = twoFactorCacheService.addTwoFactorProvider(dto.getProvider(), user.getId()
            .toString());
        if (provider.equals(TwoFactorProvider.GOOGLE_AUTHENTICATOR)) {
            final var secret = twoFactorCacheService.addGoogleAuthenticatorSecret(user.getId()
                .toString());
            final var uri = getQRCodeURI(user.getUsername(), APP_NAME, secret);
            return SendTwoFactorCodeModel.builder()
                .provider(provider)
                .secret(secret)
                .uri(uri)
                .build();
        }
        final var code = twoFactorCacheService.addTwoFactorCode(dto.getUserId()
            .toString());
        if (provider
            .equals(TwoFactorProvider.EMAIL)) {
            eventPublisher.publishEvent(new TwoFactorCodeEvent(user, code));
        }
        return SendTwoFactorCodeModel.builder()
            .provider(provider)
            .build();
    }

    public void validateTwoFactorCode(String userId, String submitCode) {
        final var provider = twoFactorCacheService.getTwoFactorProvider(userId);
        if(provider.equals(TwoFactorProvider.GOOGLE_AUTHENTICATOR)) {
            final var secret = twoFactorCacheService.getGoogleAuthenticatorSecret(userId);
            if(!validateCode(secret, submitCode))
                throw new AccessDeniedException("error.auth.two-factor");
        }else{
            final var code = twoFactorCacheService.getTwoFactorCode(userId);
            if (!submitCode.equals(code)) {
                throw new AccessDeniedException("error.auth.two-factor");
            }
        }
        twoFactorCacheService.deleteTwoFactorCode(userId);
        twoFactorCacheService.deleteTwoFactorProvider(userId);
    }

    public boolean validateCode(String secretKey, String inputCode) {
        final var WINDOW_SIZE = 1;
        try {
            long timeWindow = System.currentTimeMillis() / 30000;

            for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
                final var hash = randomUtil.generateTOTP(secretKey, timeWindow + i);
                if (Objects.equals(hash, inputCode)) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getQRCodeURI(String accountName, String issuer, String secretKey) {
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