package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Dto.RegisterDto;
import com.app.boilerplate.Shared.Account.Event.RegistrationEvent;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account")
@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {
	private final AccountService accountService;
	private final ApplicationEventPublisher eventPublisher;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/register")
	public User register(@RequestBody @Valid RegisterDto request) {
		final var user = accountService.register(request);
		eventPublisher.publishEvent(new RegistrationEvent(user, request.getLocale()));
		return user;
	}

	@PostMapping("/change-password")
	public void changePassword(@RequestBody @Valid ChangePasswordDto request) {
		accountService.changePassword(request);
	}


	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal AccessJwt jwt) {
		return accountService.profile(jwt);
	}

}
