package com.app.boilerplate.Service.Account;

import com.app.boilerplate.Decorator.Confirmcredential.ConfirmCredential;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Domain.User.User_;
import com.app.boilerplate.Exception.AuthenticatorSecretRetrievalException;
import com.app.boilerplate.Exception.BadRequestException;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Service.Authentication.TwoFactorService;
import com.app.boilerplate.Service.Authorization.AuthorizeService;
import com.app.boilerplate.Service.Revision.RevisionService;
import com.app.boilerplate.Service.Storage.StorageService;
import com.app.boilerplate.Service.Token.TokenService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Dto.EnableAuthenticatorDto;
import com.app.boilerplate.Shared.Account.Event.EmailActivationEvent;
import com.app.boilerplate.Shared.Account.Event.ResetPasswordEvent;
import com.app.boilerplate.Shared.Account.Event.SendEmailActivationEvent;
import com.app.boilerplate.Shared.Account.Model.*;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import com.app.boilerplate.Shared.Authentication.TokenType;
import com.app.boilerplate.Shared.Authorization.Model.AuthorityModel;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Shared.User.Dto.UpdateUserDto;
import com.app.boilerplate.Util.SecurityUtil;
import com.app.boilerplate.Util.StorageConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {
    private final UserService userService;
    private final IUserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenService tokenService;
    private final TwoFactorService twoFactorService;
    private final StorageService storageService;
    private final AuthorizeService authorizeService;
    private final RevisionService revisionService;

    private final Duration LOCK_OUT_TIME = Duration.ofDays(1);
    private final Integer ACCESS_FAILED_ATTEMPTS = 10;

    @ConfirmCredential
    public void changePassword(ChangePasswordDto request) {
        Optional.of(request.getId())
            .map(userService::getUserById)
            .map(user -> {
                userMapper.update(user, request.getNewPassword());
                return user;
            })
            .map((userService::save));
    }

    public RegisterResultModel register(CreateUserDto dto) {
        final var user = userService.createUser(dto, true, LoginProvider.LOCAL);
        return RegisterResultModel.builder()
            .canLogin(user.getEmailSpecify() != null)
            .build();
    }

    @ConfirmCredential
    public void enableTwoFactor(UUID userId) {
        final var user = userService.getUserById(userId);
        if (user.getIsTwoFactorEnabled())
            throw new BadRequestException("error.account.enable.two-factor");
        user.setIsTwoFactorEnabled(true);
        userService.save(user);
    }

    @ConfirmCredential
    public TwoFactorModel getTwoFactorInfo(UUID userId){
        final var user = userService.getUserById(userId);

        return TwoFactorModel.builder()
            .twoFactorEnable(user.getIsTwoFactorEnabled())
            .email(user.getEmail())
            .lastAuthenticatorUpdate(user.getLastAuthenticatorUpdate())
            .build();
    }

    @ConfirmCredential
    public void disableTwoFactor(UUID userId) {
        final var user = userService.getUserById(userId);
        user.setIsTwoFactorEnabled(false);
        userService.save(user);
    }

    @ConfirmCredential
    public void enableAuthenticator(UUID userId, EnableAuthenticatorDto authenticatorDto){
        final var user = userService.getUserById(userId);
        twoFactorService.validateAuthenticatorCode(userId, authenticatorDto.getTwoFactorCode());
        user.setLastAuthenticatorUpdate(LocalDate.now());
        userService.save(user);
    }

    @ConfirmCredential
    public void disableAuthenticator(UUID userId){
        final var user = userService.getUserById(userId);
        user.setLastAuthenticatorUpdate(null);
        tokenService.deleteAuthenticatorToken(user);
        userService.save(user);
    }

    public TOTPModel getAuthenticatorInfo(UUID userId) {
        final var user = userService.getUserById(userId);
        if(user.getLastAuthenticatorUpdate() != null)
            throw new AuthenticatorSecretRetrievalException();
        return getOrCreateAuthenticatorTokenInfo(user);
    }

    @EventListener(EmailActivationEvent.class)
    public void emailActivationEvent(EmailActivationEvent event) {
        emailActivation(event.getUser());
    }

    public void emailActivation(User user) {
        tokenService.deleteByTypeAndUser(TokenType.EMAIL_CONFIRMATION_TOKEN, user);
        final var token = tokenService.addToken(TokenType.EMAIL_CONFIRMATION_TOKEN, user);
        eventPublisher.publishEvent(new SendEmailActivationEvent(user, token.getValue()));
    }

    public void forgotPassword(String email) {
        final var user = userService.getUserByEmail(email);
        tokenService.deleteByTypeAndUser(TokenType.EMAIL_CONFIRMATION_TOKEN, user);
        final var token = tokenService.addToken(TokenType.RESET_PASSWORD_TOKEN, user);
        eventPublisher.publishEvent(new ResetPasswordEvent(user, token.getValue()));
    }

    public void resetPassword(String key, String newPassword) {
        final var token = tokenService.getTokenByTypeAndValue(TokenType.RESET_PASSWORD_TOKEN, key);
        final var user = token.getUser();
        user.setPassword(newPassword);
        user.setCredentialsNonExpired(false);
        if (user.getEmailSpecify() == null) {
            user.setEmailSpecify(LocalDateTime.now());
        }
        tokenService.deleteByValue(key);
    }

    @Async
    public void processLockoutFailed(String username) {
        final var user = userService.getUserByUsernameAndProvider(username, LoginProvider.LOCAL);
        if (!user.getIsLockoutEnabled()) return;
        if (user.getAccessFailedCount() > 0) {
            user.setAccessFailedCount(user.getAccessFailedCount() - 1);
            if (user.getAccessFailedCount() == 0) {
                user.setLockoutEndDate(LocalDateTime.now()
                    .plus(LOCK_OUT_TIME));
                user.setAccountNonLocked(false);
            }
        } else {
            user.setAccessFailedCount(ACCESS_FAILED_ATTEMPTS - 1);
            user.setAccountNonLocked(true);
            user.setLockoutEndDate(null);
        }
        userService.save(user);
    }

    @Async
    public void resetLockout(String username) {
        final var user = userService.getUserByUsernameAndProvider(username, LoginProvider.LOCAL);
        if (user.getAccessFailedCount() != ACCESS_FAILED_ATTEMPTS) {
            user.setAccessFailedCount(ACCESS_FAILED_ATTEMPTS);
            userService.save(user);
        }
    }

    public void emailVerification(String key) {
        final var token = tokenService.getTokenByTypeAndValue(TokenType.EMAIL_CONFIRMATION_TOKEN, key);
        final var user = token.getUser();
        user.setEmailSpecify(LocalDateTime.now());
        userService.save(user);
        tokenService.deleteByValue(key);
    }

    public URL avatar(UUID userId, MultipartFile file) throws IOException {
        final var user = userService.getUserById(userId);
        if (user.getImage() != null && storageService.isS3Url(user.getImage())) {
            final var oldKey = storageService.extractObjectKey(user.getImage(), StorageConsts.PUBLIC);
            storageService.delete(oldKey, StorageConsts.PUBLIC);
        }
        final var key = storageService.uploadFile(file, StorageConsts.PUBLIC, "/user/");
        final var url = storageService.getPublicUrl(key);
        user.setImage(url.toString());
        userService.save(user);
        return url;
    }

    public ProfileModel profile() throws MalformedURLException {
        final var author = authorizeService.getGrantedAuthority(SecurityUtil.getUserId()
            .toString());
        final var authorities = author.stream()
            .map(a -> {
                final var last = a.getType()
                    .substring(a.getType()
                        .lastIndexOf(".") + 1);
                return new AuthorityModel(a.getMask(), last, a.isGranting(), a.getId());
            })
            .toList();
        if(SecurityUtil.isAnonymous()){
            return ProfileModel.builder()
                .userId(SecurityUtil.getUserId())
                .authorities(authorities)
                .build();
        }
        final var user = userService.getUserById(SecurityUtil.getUserId());
        final var imageUrl = Optional.ofNullable(user.getImage())
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .flatMap(image -> {
                try {
                    return Optional.of(new URL(image));
                } catch (MalformedURLException e) {
                    return Optional.empty();
                }
            });

        return ProfileModel.builder()
            .userId(SecurityUtil.getUserId())
            .authorities(authorities)
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .avatar(imageUrl.orElse(null))
            .provider(user.getProvider())
            .build();
    }

    public void updateUserInfo(UpdateUserDto updateUserDto, UUID id){
        this.userService.putUser(updateUserDto, id);
    }

    public UpdateUserDto getUserInfoForEdit(UUID id){
        final var user = userService.getUserById(id);
        return userMapper.toUpdateUserDto(user);
    }

    public SecurityInfoModel getSecurityInfo(UUID id){
        final var lastChange = revisionService.getLastChangeDateForField(id, User_.PASSWORD, User.class);
        final var user = userService.getUserById(id);

        return SecurityInfoModel.builder()
            .twoFactorEnable(user.getIsTwoFactorEnabled())
            .passwordLastUpdate(lastChange.toLocalDate())
            .build();
    }

    protected TOTPModel getOrCreateAuthenticatorTokenInfo(User user){
        try {
            final var token = tokenService.getAuthenticatorToken(user);
            return twoFactorService.getUri(token.getValue());
        }catch (NotFoundException e){
            final var totp = twoFactorService.generateSecret(user.getUsername());
            tokenService.addAuthenticatorToken(user, totp.getSecret());
            return totp;
        }
    }
}
