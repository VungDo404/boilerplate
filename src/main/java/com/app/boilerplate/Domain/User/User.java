package com.app.boilerplate.Domain.User;

import com.app.boilerplate.Shared.User.UserListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@EntityListeners(UserListener.class)
@Table(name = "user", indexes = {
	@Index(name = "idx_user_username", columnList = User_.USERNAME)
})
public class User extends AbstractAuditable<User, UUID> implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50, unique = true, updatable = false)
    private String displayName;

    @NotNull
    @Size(min = 2, max = 50, message = "Username must be between {min} and {max} characters long")
    @Column(length = 50, nullable = false)
    private String username;

    @JsonIgnore
    @NotNull
    @Size(min = 6, message = "Password must be between {min} and {max} characters long")
    @Column(length = 60, nullable = false)
    private String password;

    @NotNull
    @Size(max = 50, message = "Email must be at most {max} characters long")
    @Email
    @Column(unique = true, length = 50, nullable = false, updatable = false)
    private String email;

    @Column(length = 100)
    private String image;

    @Column(name = "email_specify")
    private LocalDateTime emailSpecify;

    @Column(columnDefinition = "BIT default 0")
    private Boolean enabled;

    @Column(name = "account_non_locked", columnDefinition = "BIT default 0")
    private Boolean accountNonLocked;

    @Column(name = "credentials_non_expired", columnDefinition = "BIT default 0")
    private Boolean credentialsNonExpired;

	@Column(name = "account_non_expired", columnDefinition = "BIT default 0")
	private Boolean accountNonExpired;

    @Column(name = "is_two_factor_enabled", columnDefinition = "BIT default 0")
    private Boolean isTwoFactorEnabled;

    @JsonIgnore
    @Column(name = "security_stamp", length = 50)
    private String securityStamp;

    @Embedded
	@JsonUnwrapped
    private LockOut lockOut;

    @OneToMany(mappedBy = "user")
    private Set<Token> tokens;

	@Transient
	private Map<String, Object> attributes;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}


}
