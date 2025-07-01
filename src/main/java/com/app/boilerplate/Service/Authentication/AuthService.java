package com.app.boilerplate.Service.Authentication;

import com.app.boilerplate.Config.TokenAuthConfig;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Service.Token.TokenService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Authentication.*;
import com.app.boilerplate.Shared.Authentication.Dto.ConfirmCredentialsDto;
import com.app.boilerplate.Shared.Authentication.Dto.LoginDto;
import com.app.boilerplate.Shared.Authentication.Model.LoginResultModel;
import com.app.boilerplate.Util.AppConsts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenAuthConfig tokenAuthConfig;
    private final TokenService tokenService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final TwoFactorService twoFactorService;

    private final String NONE = "None";

    public LoginResultModel authenticate(LoginDto request, HttpServletResponse response) {
        final var authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(),
            request.getPassword());
        final var authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);
        SecurityContextHolder.getContext()
            .setAuthentication(authentication);
        final var user = (User) authentication.getPrincipal();
        if (user.getEmailSpecify() == null) {
            accountService.emailActivation(user);
            return LoginResultModel.builder()
                .email(user.getEmail())
                .requiresEmailVerification(true)
                .build();
        }
        if (!user.getCredentialsNonExpired()) {
            final var token = tokenService.addToken(TokenType.RESET_PASSWORD_TOKEN, user);
            return LoginResultModel.builder()
                .shouldChangePasswordOnNextLogin(true)
                .passwordResetCode(token.getValue())
                .build();
        }
        if (user.getIsTwoFactorEnabled()) {
            if (request.getTwoFactorCode() == null || request.getTwoFactorCode()
                .isBlank()) {
                return LoginResultModel.builder()
                    .isTwoFactorEnabled(true)
                    .twoFactorProviders(List.of(TwoFactorProvider.EMAIL, TwoFactorProvider.GOOGLE_AUTHENTICATOR))
                    .userId(user.getId())
                    .build();
            }
            twoFactorService.validateTwoFactorCode(user.getId(), request.getTwoFactorCode());
        }
        accountService.resetLockout(user.getUsername());
        return processLoginResult(user, response, tokenAuthConfig.getRefreshTokenExpirationInSeconds());
    }

    public LoginResultModel refreshAccessToken(String refreshToken, HttpServletResponse response) {
        final var jwt = refreshJwtDecoder(refreshToken);

        final var user = userService.getUserById(jwt.getSub());
        final var remainingDuration = Duration.between(Instant.now(), jwt.getExpiresAt());

        return processLoginResult(user, response, remainingDuration);
    }

    public void logout(HttpServletResponse response, String refreshToken) {

        ResponseCookie[] cookies = new ResponseCookie[]{
            ResponseCookie.from(AppConsts.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh-token")
                .maxAge(Duration.ZERO)
                .sameSite(NONE)
                .build(),
            ResponseCookie.from(AppConsts.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/account/profile")
                .maxAge(Duration.ZERO)
                .sameSite(NONE)
                .build(),
            ResponseCookie.from(AppConsts.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/logout")
                .maxAge(Duration.ZERO)
                .sameSite(NONE)
                .build()
        };

        for (ResponseCookie cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        if (refreshToken != null) {
            try {
                final var refreshJwt = refreshJwtDecoder(refreshToken);
                tokenService.deleteByValue(refreshJwt.getAccessJti());
                tokenService.deleteByValue(refreshJwt.getId());
            } catch (Exception ignored) {}
        }
    }

    @CachePut(value = "confirmCredentials", key = "#userId.toString()")
    public boolean confirmCredentials(ConfirmCredentialsDto confirmCredentialsDto, UUID userId) {
        authenticationManagerBuilder.getObject().authenticate(
            new UsernamePasswordAuthenticationToken(confirmCredentialsDto.getUsername(),
                confirmCredentialsDto.getPassword()));
        accountService.resetLockout(confirmCredentialsDto.getUsername());
        return true;
    }

    @Cacheable(value = "confirmCredentials",  key = "#userId.toString()")
    public boolean isConfirmCredentials(UUID userId){
        return false;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        final var jwtDecoder = NimbusJwtDecoder.withPublicKey(tokenAuthConfig.getRsaPublicKey())
            .signatureAlgorithm(SignatureAlgorithm.RS256)
            .build();

        return token -> decoder(jwtDecoder, token, AccessJwt::new);
    }

    public RefreshJwt refreshJwtDecoder(String token) {
        final var keySpec = new SecretKeySpec(tokenAuthConfig.getGetHmacSecret()
            .getBytes(StandardCharsets.UTF_8),
            AppConsts.HMAC_SHA_256);
        final var jwtDecoder =
            NimbusJwtDecoder.withSecretKey(keySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        return decoder(jwtDecoder, token, RefreshJwt::new);

    }

    public Pair<String, UUID> createAccessToken(User user) {
        final var claims = generateClaims(TokenType.ACCESS_TOKEN, user, null);
        final var accessToken = createJwtToken(claims, tokenAuthConfig.getAccessTokenExpirationInSeconds(),
            JWSAlgorithm.RS256);
        return Pair.of(accessToken, UUID.fromString((String) claims.get(AppConsts.JWT_JTI)));
    }

    private String createRefreshToken(User user, Duration expired, UUID accessTokenId) {
        final var claims = generateClaims(TokenType.REFRESH_TOKEN, user, accessTokenId);
        return createJwtToken(claims, expired, JWSAlgorithm.HS256);
    }

    private <T extends BaseJwt> T decoder(NimbusJwtDecoder decoder, String token, Function<Jwt, T> function) {
        return Optional.of(token)
            .map(decoder::decode)
            .map(function)
            .filter(this::validateCommonClaims)
            .filter(this::validateSpecificClaims)
            .orElseThrow(() -> new BadJwtException("JWT validation failed"));
    }

    private boolean validateCommonClaims(Jwt jwt) {
        return !Collections.disjoint(jwt.getAudience(), tokenAuthConfig.getAudience())
            && jwt.getIssuer()
            .sameFile(tokenAuthConfig.getIssuer());

    }

    private boolean validateSpecificClaims(Jwt jwt) {
        final var userId = UUID.fromString(jwt.getSubject());
        if (jwt instanceof AccessJwt accessJwt) {
            return validateJti(accessJwt.getJti()) ||
                validateSecurityStamp(accessJwt.getSecurityStamp(), userId);
        } else if (jwt instanceof RefreshJwt refreshJwt) {
            return validateJti(refreshJwt.getJti());
        }
        return false;
    }

    private String createJwtToken(Map<String, Object> customClaims, Duration tokenExpiration, JWSAlgorithm algorithm) {
        final var jwsHeader = new JWSHeader(algorithm);
        final var now = new Date(System.currentTimeMillis());

        final var jwtClaimsSet = new JWTClaimsSet.Builder().issuer(String.valueOf(tokenAuthConfig.getIssuer()))
            .audience(tokenAuthConfig.getAudience())
            .issueTime(now)
            .notBeforeTime(now)
            .expirationTime(Date.from(Instant.now()
                .plus(tokenExpiration)));

        customClaims.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case AppConsts.JWT_JTI:
                        jwtClaimsSet.jwtID(value.toString());
                        break;
                    case AppConsts.JWT_SUBJECT:
                        jwtClaimsSet.subject(value.toString());
                        break;
                    default:
                        jwtClaimsSet.claim(key, value);
                }
            }
        });

        final var signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet.build());

        try {
            final var signer = getSigner(algorithm);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> generateClaims(TokenType tokenType, User user, UUID associatedTokenId) {
        Assert.isTrue(tokenType == TokenType.ACCESS_TOKEN || tokenType == TokenType.REFRESH_TOKEN,
            "Invalid token type");

        final var claims = new HashMap<String, Object>();
        final var tokenId = UUID.randomUUID()
            .toString();

        if (tokenType == TokenType.ACCESS_TOKEN) {
            claims.put(AppConsts.SECURITY_STAMP, user.getSecurityStamp());
            claims.put(AppConsts.ACCESS_TOKEN_PROVIDER, user.getProvider());
            claims.put(AppConsts.ACCESS_TOKEN_USERNAME, user.getUsername());
            tokenService.addAccessToken(user, tokenId);
        } else {
            claims.put(AppConsts.ACCESS_TOKEN_ID, associatedTokenId);
            tokenService.addRefreshToken(user, tokenId);
        }

        claims.put(AppConsts.TOKEN_TYPE, tokenType.ordinal());
        claims.put(AppConsts.JWT_JTI, tokenId);
        claims.put(AppConsts.JWT_SUBJECT, user.getId());

        return claims;
    }

    private JWSSigner getSigner(JWSAlgorithm algorithm) throws JOSEException {
        if (JWSAlgorithm.Family.RSA.contains(algorithm)) {
            return new RSASSASigner(tokenAuthConfig.getRsaPrivateKey());
        } else if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
            return new MACSigner(tokenAuthConfig.getGetHmacSecret());
        }
        throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
    }

    private boolean validateSecurityStamp(String securityStamp, UUID userId) {
        return Optional.ofNullable(userId)
            .map(userService::getUserById)
            .map(user -> user.getSecurityStamp()
                .equals(securityStamp))
            .orElse(false);
    }

    private boolean validateJti(UUID jti) {
        return Optional.ofNullable(jti.toString())
            .map(tokenService::checkIfJwtExists)
            .orElse(false);
    }

    private void setRefreshTokenOnCookie(HttpServletResponse response, String value, Duration maxAge) {
        ResponseCookie[] cookies = new ResponseCookie[]{
            ResponseCookie.from(AppConsts.REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh-token")
                .maxAge(maxAge)
                .sameSite(NONE)
                .build(),
            ResponseCookie.from(AppConsts.REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(true)
                .path("/api/account/profile")
                .maxAge(maxAge)
                .sameSite(NONE)
                .build(),
            ResponseCookie.from(AppConsts.REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/logout")
                .maxAge(maxAge)
                .sameSite(NONE)
                .build()
        };
        for (ResponseCookie cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }

    public LoginResultModel processLoginResult(
        User user, HttpServletResponse response, Duration refreshTokenExpiration) {
        final var accessToken = createAccessToken(user);
        final var refreshToken = createRefreshToken(user, refreshTokenExpiration, accessToken.getRight());

        setRefreshTokenOnCookie(response, refreshToken, refreshTokenExpiration);

        return LoginResultModel.builder()
            .accessToken(accessToken.getLeft())
            .expiresInSeconds((int) tokenAuthConfig.getAccessTokenExpirationInSeconds()
                .toSeconds())
            .build();
    }

}
