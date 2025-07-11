package com.app.boilerplate.Domain.User;

import com.app.boilerplate.Shared.Authentication.Gender;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import com.app.boilerplate.Shared.Common.Identifiable;
import com.app.boilerplate.Shared.User.UserListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@Setter
@Audited
@AuditTable("user_history")
@Entity
@EntityListeners({UserListener.class})
@Table(
    name = "user",
    indexes = {
        @Index(name = "idx_user_username_provider", columnList = User_.USERNAME + ", " + User_.PROVIDER)
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_username_provider", columnNames = {User_.USERNAME, User_.PROVIDER})
    }
)
public class User implements UserDetails, Identifiable<UUID> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    private UUID id;

    @Audited(withModifiedFlag = true)
    @Size(min = 2, max = 50, message = "DisplayName must be between {min} and {max} characters long")
    @Column(length = 50, nullable = false)
    private String displayName;

    @NotAudited
    @NotNull
    @Size(min = 2, max = 50, message = "Username must be between {min} and {max} characters long")
    @Column(length = 50, nullable = false, updatable = false)
    private String username;


    @Audited(withModifiedFlag = true)
    @JsonIgnore
    @NotNull
    @Size(min = 6, message = "Password must be between {min} and {max} characters long")
    @Column(length = 60, nullable = false)
    private String password;

    @NotAudited
    @NotNull
    @Size(max = 50, message = "Email must be at most {max} characters long")
    @Email
    @Column(length = 50, nullable = false, updatable = false)
    private String email;

    @Audited(withModifiedFlag = true)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Audited(withModifiedFlag = true)
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Audited(withModifiedFlag = true)
    @Column(columnDefinition = "TINYINT")
    private Gender gender;

    @Audited(withModifiedFlag = true)
    @Column(length = 100)
    private String image;

    @Audited(withModifiedFlag = true)
    @Column(name = "email_specify")
    private LocalDateTime emailSpecify;

    @Audited(withModifiedFlag = true)
    @Column(columnDefinition = "BIT default 1", nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Audited(withModifiedFlag = true)
    @Column(name = "account_non_locked", columnDefinition = "BIT default 1", nullable = false)
    private Boolean accountNonLocked = Boolean.TRUE;

    @Audited(withModifiedFlag = true)
    @Column(name = "credentials_non_expired", columnDefinition = "BIT default 1", nullable = false)
    private Boolean credentialsNonExpired = Boolean.TRUE;

    @Audited(withModifiedFlag = true)
    @Column(name = "account_non_expired", columnDefinition = "BIT default 1", nullable = false)
    private Boolean accountNonExpired = Boolean.TRUE;

    @Audited(withModifiedFlag = true)
    @Column(name = "is_two_factor_enabled", columnDefinition = "BIT default 0", nullable = false)
    private Boolean isTwoFactorEnabled = Boolean.FALSE;

    @NotAudited
    @Column(name = "last_authenticator_update")
    private LocalDate lastAuthenticatorUpdate;

    @NotAudited
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "TINYINT", updatable = false, nullable = false)
    private LoginProvider provider;

    @NotAudited
    @JsonIgnore
    @Column(name = "security_stamp", length = 50, nullable = false)
    private String securityStamp;

    @Audited(withModifiedFlag = true)
    @Column(name = "is_lockout_enabled", columnDefinition = "BIT default 0", nullable = false)
    private Boolean isLockoutEnabled = Boolean.FALSE;

    @Audited(withModifiedFlag = true)
    @Column(name = "access_failed_count", columnDefinition = "SMALLINT DEFAULT 5", nullable = false)
    private int accessFailedCount = 10;

    @Audited(withModifiedFlag = true)
    @Column(name = "lockout_end_date")
    private LocalDateTime lockoutEndDate;

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
