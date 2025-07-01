package com.app.boilerplate.Shared.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Service.Authorization.AccessControlListService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserListener {
	private final PasswordEncoder passwordEncoder;
	private final AccessControlListService accessControlListService;


	@PrePersist
	@PreUpdate
	public void hashPassword(User user) {
		if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
	}

	@PostPersist
	public void addNewSidForUser(User user) {
		final var sidName = user.getProvider() + ":" + user.getUsername();
		accessControlListService.callCreateOrRetrieveSidPrimaryKey(sidName, true, true);
	}
}
