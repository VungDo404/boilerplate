package com.app.boilerplate.Config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class TokenAuthConfig {
	@Value("${rsa.public-key}")
	private RSAPublicKey rsaPublicKey;
	@Value("${rsa.private-key}")
	private RSAPrivateKey rsaPrivateKey;
	@Value("${server.base-url}")
	private List<String> audience;
	@Value("${server.base-url}")
	private URL issuer;
	@Value("${symmetric.security-key}")
	private String getHmacSecret;
	private final Duration accessTokenExpirationInSeconds = Duration.ofHours(1);
	private final Duration refreshTokenExpirationInSeconds = Duration.ofHours(31);

}
