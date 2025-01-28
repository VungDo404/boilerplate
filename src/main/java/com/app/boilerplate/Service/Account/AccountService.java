package com.app.boilerplate.Service.Account;

import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Dto.ChangePasswordDto;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {
	private final UserService userService;
	private final IUserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public void changePassword(ChangePasswordDto request) {
		Optional.of(request.getId())
			.map(userService::getUserById)
			.filter(user -> passwordEncoder.matches(request.getCurrentPassword(),
				user.getPassword()))
			.map(user -> {
				userMapper.update(user, request.getNewPassword());
				return user;
			})
			.map((userService::save));
	}

	public String profile(AccessJwt jwt) {
		return jwt.getSecurityStamp();
	}



}
