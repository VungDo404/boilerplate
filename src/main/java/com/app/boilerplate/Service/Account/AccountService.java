package com.app.boilerplate.Service.Account;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Service.Token.TokenService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Event.EmailActivationEvent;
import com.app.boilerplate.Shared.Account.Event.ResetPasswordEvent;
import com.app.boilerplate.Shared.Account.Event.SendEmailActivationEvent;
import com.app.boilerplate.Shared.Account.Model.RegisterResultModel;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
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

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {
	private final UserService userService;
	private final IUserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final TokenService tokenService;

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
		final var user = userService.createUser(dto, true);
		return RegisterResultModel.builder()
				.canLogin(user.getEmailSpecify() != null)
				.build();
	}

	@EventListener(EmailActivationEvent.class)
	public void emailActivationEvent(EmailActivationEvent event) {
		emailActivation(event.getUser());
	}

	public void emailActivation(User user){
		tokenService.deleteByTypeAndUser(TokenType.EmailConfirmationToken, user);
		final var key = tokenService.addToken(TokenType.EmailConfirmationToken, user);
		eventPublisher.publishEvent(new SendEmailActivationEvent(user, key));
	}

	public void forgotPassword(String email) {
		final var user = userService.getUserByEmail(email);
		final var key = tokenService.addToken(TokenType.ResetPasswordToken, user);
		eventPublisher.publishEvent(new ResetPasswordEvent(user, key));
	}

	public void resetPassword(String key, String newPassword){
		final var token = tokenService.getTokenByTypeAndValue(TokenType.ResetPasswordToken, key);
		final var user = token.getUser();
		user.setPassword(newPassword);
		tokenService.deleteByValue(key);
	}

	public void emailVerification(String key) {
		final var token = tokenService.getTokenByTypeAndValue(TokenType.EmailConfirmationToken, key);
		final var user = token.getUser();
		user.setEmailSpecify(LocalDateTime.now());
		userService.save(user);
		tokenService.deleteByValue(key);
	}

	public String profile(AccessJwt jwt) {
		return jwt.getSecurityStamp();
	}
}
