package com.app.boilerplate.Shared.User;

import com.app.boilerplate.Domain.User.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserListener {
	private final PasswordEncoder passwordEncoder;

	@PrePersist
	@PreUpdate
	public void hashPassword(User user) {
		if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
	}
}
