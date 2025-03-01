package com.app.boilerplate.Service.Account;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Exception.BadRequestException;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Service.Authentication.TwoFactorService;
import com.app.boilerplate.Service.Token.TokenService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Event.EmailActivationEvent;
import com.app.boilerplate.Shared.Account.Event.ResetPasswordEvent;
import com.app.boilerplate.Shared.Account.Event.SendEmailActivationEvent;
import com.app.boilerplate.Shared.Account.Model.RegisterResultModel;
import com.app.boilerplate.Shared.Account.Model.TOTPModel;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import com.app.boilerplate.Shared.Authentication.TokenType;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {
	private final UserService userService;
	private final IUserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final TokenService tokenService;
	private final TwoFactorService twoFactorService;

	public void changePassword(ChangePasswordDto request) {
		Optional.of(request.getId())
			.map(userService::getUserById)
			.filter(user -> passwordEncoder.matches(request.getCurrentPassword(),
				user.getPassword()))
			.map(user -> {
				userMapper.update(user, request.getNewPassword());
				return user;
			})
			.map((userService::save));
	}

	public RegisterResultModel register(CreateUserDto dto){
		final var user = userService.createUser(dto, true, LoginProvider.LOCAL);
		return RegisterResultModel.builder()
				.canLogin(user.getEmailSpecify() != null)
				.build();
	}

	public TOTPModel enableTwoFactor(UUID userId){
		final var user = userService.getUserById(userId);
		if(user.getIsTwoFactorEnabled())
			throw new BadRequestException("error.account.enable.two-factor");
		user.setIsTwoFactorEnabled(true);
		final var totp = twoFactorService.generateSecret(user.getUsername());
		tokenService.addAuthenticatorToken(user,totp.getSecret());
		return totp;
	}

	public void disableTwoFactor(UUID userId){
		final var user = userService.getUserById(userId);
		user.setIsTwoFactorEnabled(false);
		tokenService.deleteAuthenticatorToken(user);
	}

	public TOTPModel getTwoFactor(UUID userId){
		final var user = userService.getUserById(userId);
		final var token = tokenService.getAuthenticatorToken(user);
		return twoFactorService.getUri(token.getValue());
	}

	@EventListener(EmailActivationEvent.class)
	public void emailActivationEvent(EmailActivationEvent event) {
		emailActivation(event.getUser());
	}

	public void emailActivation(User user){
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

	public void resetPassword(String key, String newPassword){
		final var token = tokenService.getTokenByTypeAndValue(TokenType.RESET_PASSWORD_TOKEN, key);
		final var user = token.getUser();
		user.setPassword(newPassword);
		user.setCredentialsNonExpired(false);
		if(user.getEmailSpecify() == null){
			user.setEmailSpecify(LocalDateTime.now());
		}
		tokenService.deleteByValue(key);
	}

	public void emailVerification(String key) {
		final var token = tokenService.getTokenByTypeAndValue(TokenType.EMAIL_CONFIRMATION_TOKEN, key);
		final var user = token.getUser();
		user.setEmailSpecify(LocalDateTime.now());
		userService.save(user);
		tokenService.deleteByValue(key);
	}

	public String profile(AccessJwt jwt) {
		return jwt.getSecurityStamp();
	}
}
