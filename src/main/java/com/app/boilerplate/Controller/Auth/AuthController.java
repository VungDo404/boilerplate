package com.app.boilerplate.Controller.Auth;

import com.app.boilerplate.Decorator.RateLimit.RateLimit;
import com.app.boilerplate.Service.Authentication.AuthService;
import com.app.boilerplate.Service.Authentication.TwoFactorService;
import com.app.boilerplate.Shared.Authentication.Dto.ConfirmCredentialsDto;
import com.app.boilerplate.Shared.Authentication.Dto.LoginDto;
import com.app.boilerplate.Shared.Authentication.Dto.SendTwoFactorCodeDto;
import com.app.boilerplate.Shared.Authentication.Model.ConfirmCredentialModel;
import com.app.boilerplate.Shared.Authentication.Model.LoginResultModel;
import com.app.boilerplate.Util.AppConsts;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Tag(name = "Auth")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    private final TwoFactorService twoFactorService;

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'authenticate-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.READ +
        "')")
    @PostMapping("/authenticate")
    public LoginResultModel authenticate(@RequestBody @Valid LoginDto request, HttpServletResponse response) {
        return authService.authenticate(request, response);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'refreshToken-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.DELETE +
        "')")
    @PostMapping("/refresh-token")
    public LoginResultModel refreshToken(
        @CookieValue(AppConsts.REFRESH_TOKEN) String refreshToken,
        HttpServletResponse response) {
        return authService.refreshAccessToken(refreshToken, response);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'logout-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.DELETE +
        "')")
    @PostMapping("/logout")
    public void logout(
        HttpServletResponse response,
        @CookieValue(value = AppConsts.REFRESH_TOKEN, required = false) String refreshToken) {
        authService.logout(response, refreshToken);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'sendTwoFactorCode-'" +
        " + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.READ +
        "')")
    @PostMapping("/send-code")
    public void sendTwoFactorCode(@RequestBody @Valid SendTwoFactorCodeDto request) {
        twoFactorService.sendTwoFactorCode(request);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'authenticate-' + #ip")
    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @PostMapping("/confirm-credential/{id}")
    public void confirmSecurityAction(
        @PathVariable @NotNull UUID id,
        @RequestBody @Valid ConfirmCredentialsDto request) {
        authService.confirmCredentials(request, id);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'authenticate-' + #ip")
    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @GetMapping("/confirm-credential/{id}")
    public ConfirmCredentialModel isConfirmSecurity(@PathVariable @NotNull UUID id){
        final var res = authService.isConfirmCredentials(id);
        return ConfirmCredentialModel.builder()
            .confirmed(res)
            .build();
    }
}
