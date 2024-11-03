package com.app.boilerplate.Util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public final class RandomUtil {
    public String randomPassword(){
        return RandomStringUtils.randomAlphanumeric(10);
    }
    public String randomToken() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] randomBytes = new byte[18];
        secureRandom.nextBytes(randomBytes);

        final long timestamp = System.currentTimeMillis();
        final byte[] timestampBytes = ByteBuffer.allocate(8).putLong(timestamp).array();

        final byte[] combined = ByteBuffer.allocate(26)
                .put(timestampBytes)
                .put(randomBytes)
                .array();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }
}
