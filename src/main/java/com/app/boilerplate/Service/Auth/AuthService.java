package com.app.boilerplate.Service.Auth;

import com.app.boilerplate.Config.TokenAuthConfig;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Service.Token.TokenService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Shared.Authentication.BaseJwt;
import com.app.boilerplate.Shared.Authentication.Dto.LoginDto;
import com.app.boilerplate.Shared.Authentication.Model.LoginResultModel;
import com.app.boilerplate.Shared.Authentication.Model.RefreshAccessTokenModel;
import com.app.boilerplate.Shared.Authentication.RefreshJwt;
import com.app.boilerplate.Shared.Authentication.TokenType;
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
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;

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

        }
        final var refreshToken = createRefreshToken(user, tokenAuthConfig.getRefreshTokenExpirationInSeconds());
        final var accessToken = createAccessToken(user, refreshToken.getRight());

        setRefreshTokenOnCookie(response, refreshToken.getRight()
                        .toString(),
                tokenAuthConfig.getRefreshTokenExpirationInSeconds());

        return LoginResultModel.builder()
                .accessToken(accessToken)
                .encryptedAccessToken("")
                .expiresInSeconds((int) tokenAuthConfig.getAccessTokenExpirationInSeconds()
                        .toSeconds())
                .isTwoFactorEnabled(false)
                .build();
    }

    public RefreshAccessTokenModel refreshAccessToken(String refreshToken, HttpServletResponse response) {
        final var jwt = refreshJwtDecoder(refreshToken);

        final var user = userService.getUserById(jwt.getSub());
        final var remainingDuration = Duration.between(Instant.now(), jwt.getExpiresAt());

        final var rotateRefreshToken = createRefreshToken(user, remainingDuration);
        final var accessToken = createAccessToken(user, rotateRefreshToken.getRight());

        setRefreshTokenOnCookie(response, rotateRefreshToken.getRight()
                .toString(), remainingDuration);

        return RefreshAccessTokenModel.builder()
                .accessToken(accessToken)
                .expiresInSeconds((int) tokenAuthConfig.getAccessTokenExpirationInSeconds()
                        .toSeconds())
                .build();
    }

    public void logout(AccessJwt accessJwt, HttpServletResponse response, String refreshToken) {
        final var refreshJwt = refreshJwtDecoder(refreshToken);

        ResponseCookie[] cookies = new ResponseCookie[]{
                ResponseCookie.from(AppConsts.REFRESH_TOKEN, "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/auth/refresh-token")
                        .maxAge(Duration.ZERO)
                        .sameSite("Lax")
                        .build(),
                ResponseCookie.from(AppConsts.REFRESH_TOKEN, "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/account/profile")
                        .maxAge(Duration.ZERO)
                        .sameSite("Lax")
                        .build(),
                ResponseCookie.from(AppConsts.REFRESH_TOKEN, "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/auth/logout")
                        .maxAge(Duration.ZERO)
                        .sameSite("Lax")
                        .build()
        };

        for (ResponseCookie cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        tokenService.deleteByValue(accessJwt.getId());
        tokenService.deleteByValue(refreshJwt.getId());

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
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();

        return decoder(jwtDecoder, token, RefreshJwt::new);

    }

    public String createAccessToken(User user, UUID refreshTokenId) {
        final var claims = generateClaims(TokenType.ACCESS_TOKEN, user, refreshTokenId);
        return createJwtToken(claims, tokenAuthConfig.getAccessTokenExpirationInSeconds(), JWSAlgorithm.RS256);
    }

    private Pair<String, UUID> createRefreshToken(User user, Duration expired) {
        final var claims = generateClaims(TokenType.REFRESH_TOKEN, user, null);
        final var refreshToken = createJwtToken(claims, expired, JWSAlgorithm.HS256);
        return Pair.of(refreshToken, UUID.fromString((String) claims.get(AppConsts.JWT_JTI)));
    }

    private <T extends BaseJwt> T decoder(NimbusJwtDecoder decoder, String token, Function<Jwt, T> function) {
        return Optional.of(token)
                .map(decoder::decode)
                .map(function)
                .filter(this::validateCommonClaims)
                .filter(this::validateSpecificClaims)
                .orElseThrow(() -> new JwtException("JWT validation failed"));
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

    private Map<String, Object> generateClaims(TokenType tokenType, User user, UUID refreshTokenId) {
        Assert.isTrue(tokenType == TokenType.ACCESS_TOKEN || tokenType == TokenType.REFRESH_TOKEN,
                "Invalid token type");

        final var claims = new HashMap<String, Object>();
        final var tokenId = UUID.randomUUID()
                .toString();

        if (tokenType == TokenType.ACCESS_TOKEN) {
            claims.put(AppConsts.REFRESH_TOKEN_ID, refreshTokenId);
            claims.put(AppConsts.SECURITY_STAMP, user.getSecurityStamp());
            claims.put(AppConsts.ACCESS_TOKEN_PROVIDER, user.getProvider());
            claims.put(AppConsts.ACCESS_TOKEN_USERNAME, user.getUsername());
            tokenService.addAccessToken(user, tokenId);
        } else {
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
                        .path("/auth/refresh-token")
                        .maxAge(maxAge)
                        .sameSite("Lax")
                        .build(),
                ResponseCookie.from(AppConsts.REFRESH_TOKEN, value)
                        .httpOnly(true)
                        .secure(true)
                        .path("/account/profile")
                        .maxAge(maxAge)
                        .sameSite("Lax")
                        .build(),
                ResponseCookie.from(AppConsts.REFRESH_TOKEN, value)
                        .httpOnly(true)
                        .secure(true)
                        .path("/auth/logout")
                        .maxAge(maxAge)
                        .sameSite("Lax")
                        .build()
        };
        for (ResponseCookie cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }

}
