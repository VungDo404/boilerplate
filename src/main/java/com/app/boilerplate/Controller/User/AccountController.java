package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Group.RegisterUser;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account")
@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {
	private final AccountService accountService;
	private final UserService userService;

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.CREATE +
		"')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/register")
	public User register(@RequestBody @Validated(RegisterUser.class) CreateUserDto request) {
		return userService.createUser(request, true);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
		"')")
	@PostMapping("/change-password")
	public void changePassword(@RequestBody @Valid ChangePasswordDto request) {
		accountService.changePassword(request);
	}

	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal AccessJwt jwt) {
		return accountService.profile(jwt);
	}

}
