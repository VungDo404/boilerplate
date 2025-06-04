package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Decorator.FileValidator.ValidFile;
import com.app.boilerplate.Service.Account.AccountService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Account.Dto.EmailDto;
import com.app.boilerplate.Shared.Account.Dto.ResetPasswordDto;
import com.app.boilerplate.Shared.Account.Group.RegisterUser;
import com.app.boilerplate.Shared.Account.Model.ProfileModel;
import com.app.boilerplate.Shared.Account.Model.RegisterResultModel;
import com.app.boilerplate.Shared.Account.Model.TOTPModel;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
    public RegisterResultModel register(
        @RequestBody @Validated({RegisterUser.class, Default.class}) CreateUserDto request) {
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

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @PutMapping("/code/user/{id}")
    public TOTPModel enableTwoFactor(@PathVariable @NotNull UUID id) {
        return accountService.enableTwoFactor(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @GetMapping("/code/user/{id}")
    public TOTPModel getTwoFactorIfEnable(@PathVariable @NotNull UUID id) {
        return accountService.getTwoFactor(id);
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @DeleteMapping("/code/user/{id}")
    public void disableTwoFactor(@PathVariable @NotNull UUID id) {
        accountService.disableTwoFactor(id);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHENTICATION + "', '" + PermissionUtil.WRITE +
        "')")
    @GetMapping("/profile")
    public ProfileModel profile() throws MalformedURLException {
        return accountService.profile();
    }

    @PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
    @PutMapping(value = "/avatar/user/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public URL avatar(
        @PathVariable @NotNull UUID id,
        @ValidFile(maxSize = "1MB", allowedContentTypes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
        @RequestPart(value = "file")
        MultipartFile file) throws IOException {
        return accountService.avatar(id, file);
    }

}
