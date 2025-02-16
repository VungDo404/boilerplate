package com.app.boilerplate.Shared.User.Dto;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@Getter
public class PostUserDto extends CreateUserDto {
    private final String image;
    private final Boolean enabled;
    private final Boolean blocked;
    private final Boolean shouldChangePasswordOnNextLogin;
    private final Boolean isTwoFactorEnabled;
    private final Boolean isLockoutEnabled;
    private final int accessFailedCount;
    private final LocalDateTime lockoutEndDate;
    private final Boolean shouldSendConfirmationEmail;

    public PostUserDto(
            String username,
            String password,
            String email,
            String displayName,
            String image,
            Boolean enabled,
            Boolean blocked,
            Boolean shouldChangePasswordOnNextLogin,
            Boolean isTwoFactorEnabled,
            Boolean isLockoutEnabled,
            int accessFailedCount,
            LocalDateTime lockoutEndDate,
            Boolean shouldSendConfirmationEmail
    ) {
        super(username, password, email, displayName);
        this.image = image;
        this.enabled = enabled;
        this.blocked = blocked;
        this.shouldChangePasswordOnNextLogin = shouldChangePasswordOnNextLogin;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.isLockoutEnabled = isLockoutEnabled;
        this.accessFailedCount = accessFailedCount;
        this.lockoutEndDate = lockoutEndDate;
        this.shouldSendConfirmationEmail = shouldSendConfirmationEmail;
    }
}
