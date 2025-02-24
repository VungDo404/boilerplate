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
	@Value("${security-key.rsa.public-key}")
	private RSAPublicKey rsaPublicKey;
	@Value("${security-key.rsa.private-key}")
	private RSAPrivateKey rsaPrivateKey;
	@Value("${server.base-url}")
	private List<String> audience;
	@Value("${server.base-url}")
	private URL issuer;
	@Value("${security-key.refresh-token}")
	private String getHmacSecret;
	private final Duration accessTokenExpirationInSeconds = Duration.ofHours(1);
	private final Duration refreshTokenExpirationInSeconds = Duration.ofHours(31);

}
