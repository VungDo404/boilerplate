package com.app.boilerplate.Shared.User.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */

@Setter
@Getter
public class PostUserDto extends CreateUserDto {
    private final String image;
    private final LocalDateTime emailSpecify;
    private final Boolean enabled;
    private final Boolean accountNonLocked;
    private final Boolean credentialsNonExpired;
    private final Boolean accountNonExpired;
    private final Boolean shouldChangePasswordOnNextLogin;
    private final Boolean isTwoFactorEnabled;
    private final Boolean isLockoutEnabled;
    private final Integer accessFailedCount;
    private final LocalDateTime lockoutEndDate;
    private final Boolean shouldSendConfirmationEmail;

    public PostUserDto(
        String username,
        String password,
        String email,
        String displayName,
        String image,
        LocalDateTime emailSpecify,
        Boolean enabled,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Boolean accountNonExpired,
        Boolean shouldChangePasswordOnNextLogin,
        Boolean isTwoFactorEnabled,
        Boolean isLockoutEnabled,
        Integer accessFailedCount,
        LocalDateTime lockoutEndDate,
        Boolean shouldSendConfirmationEmail
    ) {
        super(username, password, email, displayName);
        this.image = image;
        this.emailSpecify = emailSpecify;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonExpired = accountNonExpired;
        this.shouldChangePasswordOnNextLogin = shouldChangePasswordOnNextLogin;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.isLockoutEnabled = isLockoutEnabled;
        this.accessFailedCount = accessFailedCount;
        this.lockoutEndDate = lockoutEndDate;
        this.shouldSendConfirmationEmail = shouldSendConfirmationEmail;

    }
}
