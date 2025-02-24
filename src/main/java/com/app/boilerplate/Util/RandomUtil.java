package com.app.boilerplate.Util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public final class RandomUtil {
    public static final int CODE_DIGITS = 6;
    public String randomPassword(){
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public String randomToken() {
        final var secureRandom = new SecureRandom();
        final var randomBytes = new byte[26];
        secureRandom.nextBytes(randomBytes);

        final var timestamp = System.currentTimeMillis();
        final var timestampBytes = ByteBuffer.allocate(8).putLong(timestamp).array();

        final var insertIndex = secureRandom.nextInt(26 - 8);
        System.arraycopy(timestampBytes, 0, randomBytes, insertIndex, 8);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public  String generateSecretKey() {
        final var random = new SecureRandom();
        final var bytes = new byte[20];
        random.nextBytes(bytes);

        final var base32 = new Base32();
        return base32.encodeToString(bytes).replace("=", "");
    }

    public String getTOTPCode(String secretKey) {
        final var timeIndex = System.currentTimeMillis() / 30000;
        return generateTOTP(secretKey, timeIndex);
    }

    public String generateTOTP(String secret, long timeIndex) {
        final var HASH_ALGORITHM = "HmacSHA1";
        try {
            final var base32 = new Base32();
            final var bytes = base32.decode(secret);

            final var data = new byte[8];
            var value = timeIndex;
            for (int i = 8; i-- > 0; value >>>= 8) {
                data[i] = (byte) value;
            }

            final var signKey = new SecretKeySpec(bytes, HASH_ALGORITHM);
            final var mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(signKey);
            final var hash = mac.doFinal(data);

            final var offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= hash[offset + i] & 0xFF;
            }

            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= (long) Math.pow(10, CODE_DIGITS);

            return String.format("%0" + CODE_DIGITS + "d", truncatedHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


}
