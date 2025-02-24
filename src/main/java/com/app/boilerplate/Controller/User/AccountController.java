package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Dto.EmailDto;
import com.app.boilerplate.Shared.Account.Dto.ResetPasswordDto;
import com.app.boilerplate.Shared.Account.Group.RegisterUser;
import com.app.boilerplate.Shared.Account.Model.RegisterResultModel;
import com.app.boilerplate.Shared.Account.Model.TOTPModel;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Account")
@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.CREATE +
            "')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResultModel register(@RequestBody @Validated({RegisterUser.class, Default.class}) CreateUserDto request) {
        return accountService.register(request);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
            "')")
    @GetMapping("/email-activation")
    public void emailActivation(@RequestParam("key") String key) {
        accountService.emailVerification(key);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
            "')")
    @PostMapping("/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordDto request) {
        accountService.changePassword(request);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
            "')")
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody @Valid EmailDto request) {
        accountService.forgotPassword(request.getEmail());
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
            "')")
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody @Valid ResetPasswordDto request, @RequestParam("key") String key) {
        accountService.resetPassword(key, request.getPassword());
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
        "')")
    @PutMapping("/code/user/{id}")
    public TOTPModel enableTwoFactor(@PathVariable @NotNull UUID id) {
        return accountService.enableTwoFactor(id);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
        "')")
    @DeleteMapping("/code/user/{id}")
    public void disableTwoFactor(@PathVariable @NotNull UUID id) {
        accountService.disableTwoFactor(id);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
            "')")
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal AccessJwt jwt) {
        return accountService.profile(jwt);
    }

}
