package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Decorator.FileValidator.ValidFile;
import com.app.boilerplate.Decorator.RateLimit.RateLimit;
import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Dto.EmailDto;
import com.app.boilerplate.Shared.Account.Dto.EnableAuthenticatorDto;
import com.app.boilerplate.Shared.Account.Dto.ResetPasswordDto;
import com.app.boilerplate.Shared.Account.Group.RegisterUser;
import com.app.boilerplate.Shared.Account.Model.*;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Shared.User.Dto.UpdateUserDto;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Account")
@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {
    private final AccountService accountService;

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'register-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.CREATE +
        "')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResultModel register(
        @RequestBody @Validated({RegisterUser.class, Default.class}) CreateUserDto request) {
        return accountService.register(request);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'emailActivation-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
        "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/email-activation")
    public void emailActivation(@RequestParam("key") String key) {
        accountService.emailVerification(key);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'changePassword-' + #ip")
    @PreAuthorize("hasPermission(#request.id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE +
        "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordDto request) {
        accountService.changePassword(request);
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'forgotPassword-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
        "')")
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody @Valid EmailDto request) {
        accountService.forgotPassword(request.getEmail());
    }

    @RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'resetPassword-' + #ip")
    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
        "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody @Valid ResetPasswordDto request, @RequestParam("key") String key) {
        accountService.resetPassword(key, request.getPassword());
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @GetMapping("/code/user/{id}")
    public TwoFactorModel getTwoFactor(@PathVariable @NotNull UUID id) {
        return accountService.getTwoFactorInfo(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/code/user/{id}")
    public void enableTwoFactor(@PathVariable @NotNull UUID id) {
        accountService.enableTwoFactor(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/code/user/{id}")
    public void disableTwoFactor(@PathVariable @NotNull UUID id) {
        accountService.disableTwoFactor(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @GetMapping("/authenticator/user/{id}")
    public TOTPModel getAuthenticator(@PathVariable @NotNull UUID id) {
        return accountService.getAuthenticatorInfo(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/authenticator/user/{id}")
    public void enableAuthenticator(@PathVariable @NotNull UUID id, @RequestBody @Valid EnableAuthenticatorDto request) {
        accountService.enableAuthenticator(id, request);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/authenticator/user/{id}")
    public void disableAuthenticator(@PathVariable @NotNull UUID id) {
        accountService.disableAuthenticator(id);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.DELETE +
        "')")
    @GetMapping("/profile")
    public ProfileModel getUserInfo() throws MalformedURLException {
        return accountService.profile();
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @PatchMapping(value = "/avatar/user/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public URL avatar(
        @PathVariable @NotNull UUID id,
        @ValidFile(maxSize = "1MB", allowedContentTypes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
        @RequestPart(value = "file")
        MultipartFile file) throws IOException, MimeTypeException {
        return accountService.avatar(id, file);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/user/{id}")
    public void updateUserInfo(@RequestBody @Valid UpdateUserDto request, @PathVariable @NotNull UUID id){
        accountService.updateUserInfo(request, id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @GetMapping("/user/{id}")
    public UpdateUserDto getInfoForEdit(@PathVariable @NotNull UUID id){
        return accountService.getUserInfoForEdit(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @GetMapping("/user/{id}/security")
    public SecurityInfoModel getSecurityInfo(@PathVariable @NotNull UUID id) {
        return accountService.getSecurityInfo(id);
    }
}
