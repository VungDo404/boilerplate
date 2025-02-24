package com.app.boilerplate.Service.Authentication;

import com.app.boilerplate.Shared.Authentication.TwoFactorProvider;
import com.app.boilerplate.Util.CacheConsts;
import com.app.boilerplate.Util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TwoFactorCacheService {
    private final RandomUtil randomUtil;

    @CachePut(value = CacheConsts.TWO_FACTOR, key = "#userId")
    public String addTwoFactorCode(String userId) {
        final var secret = randomUtil.generateSecretKey();
        return randomUtil.getTOTPCode(secret);
    }

    @CacheEvict(value = CacheConsts.TWO_FACTOR, key = "#userId")
    public void deleteTwoFactorCode(String userId) {}

    @Cacheable(value = CacheConsts.TWO_FACTOR, key = "#userId")
    public String getTwoFactorCode(String userId) {return "";}

    @CachePut(value = CacheConsts.PROVIDER, key = "#userId")
    public TwoFactorProvider addTwoFactorProvider(TwoFactorProvider twoFactorProvider, String userId) {
        return twoFactorProvider;
    }

    @Cacheable(value = CacheConsts.PROVIDER, key = "#userId")
    public TwoFactorProvider getTwoFactorProvider(String userId) {return null;}

    @CacheEvict(value = CacheConsts.PROVIDER, key = "#userId")
    public void deleteTwoFactorProvider( String userId) {}
}