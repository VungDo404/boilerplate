package com.app.boilerplate.Shared.User.Dto;

import com.app.boilerplate.Shared.Authentication.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutUserDto extends UpdateUserDto implements Serializable {
    private final Boolean isTwoFactorEnabled;
    private final Boolean accountNonLocked;
    private final Boolean credentialsNonExpired;
    private final Boolean accountNonExpired;
    private final Boolean lockOutIsLockoutEnabled;
    private final int lockOutAccessFailedCount;
    private final LocalDateTime lockOutLockoutEndDate;

    public PutUserDto(
        final String displayName,
        final String phoneNumber,
        final Gender gender,
        final LocalDate dateOfBirth,
        final Boolean isTwoFactorEnabled,
        final Boolean accountNonLocked,
        final Boolean credentialsNonExpired,
        final Boolean accountNonExpired,
        final Boolean lockOutIsLockoutEnabled,
        final int lockOutAccessFailedCount,
        final LocalDateTime lockOutLockoutEndDate
                     ) {
        super(displayName, phoneNumber, gender, dateOfBirth);
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonExpired = accountNonExpired;
        this.lockOutIsLockoutEnabled = lockOutIsLockoutEnabled;
        this.lockOutAccessFailedCount = lockOutAccessFailedCount;
        this.lockOutLockoutEndDate = lockOutLockoutEndDate;
    }
}
