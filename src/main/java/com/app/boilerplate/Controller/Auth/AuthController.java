package com.app.boilerplate.Controller.Auth;

import com.app.boilerplate.Service.Authentication.AuthService;
import com.app.boilerplate.Service.Authentication.TwoFactorService;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Shared.Authentication.Dto.LoginDto;
import com.app.boilerplate.Shared.Authentication.Dto.SendTwoFactorCodeDto;
import com.app.boilerplate.Shared.Authentication.Model.LoginResultModel;
import com.app.boilerplate.Shared.Authentication.Model.RefreshAccessTokenModel;
import com.app.boilerplate.Util.AppConsts;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    private final TwoFactorService twoFactorService;

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.READ +
        "')")
    @PostMapping("/authenticate")
    public LoginResultModel authenticate(@RequestBody @Valid LoginDto request, HttpServletResponse response) {
        return authService.authenticate(request, response);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.DELETE +
        "')")
    @PostMapping("/refresh-token")
    public RefreshAccessTokenModel refreshToken(@CookieValue(AppConsts.REFRESH_TOKEN) String refreshToken,
                                                HttpServletResponse response) {
        return authService.refreshAccessToken(refreshToken, response);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.DELETE +
        "')")
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal AccessJwt jwt, HttpServletResponse response,
                       @CookieValue(AppConsts.REFRESH_TOKEN) String refreshToken) {
        authService.logout(jwt, response, refreshToken);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.READ +
        "')")
    @PostMapping("/send-code")
    public void sendTwoFactorCode(@RequestBody @Valid SendTwoFactorCodeDto request) {
        twoFactorService.sendTwoFactorCode(request);
    }
}
