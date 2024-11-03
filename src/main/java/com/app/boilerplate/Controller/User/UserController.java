package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Account.Event.RegistrationEvent;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.PutUserDto;
import com.app.boilerplate.Shared.User.Dto.UserCriteriaDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "User")
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;
	private final IUserMapper userMapper;

	@GetMapping("/")
	public Page<User> getUsers(Optional<UserCriteriaDto> userCriteriaDto,
							   @ParameterObject Pageable pageable) {
		return userService.getAllUsers(userCriteriaDto, pageable);
	}

	@GetMapping("/{id}")
	public User getUserById(@PathVariable @NotNull UUID id) {
		return userService.getUserById(id);
	}

	@ResponseStatus(CREATED)
	@PostMapping
	public User createUser(@RequestBody @Valid PostUserDto request) {
		final var userRequest = userMapper.toUser(request);
		final var user = userService.createUser(userRequest);
		if (request.getShouldSendConfirmationEmail())
			eventPublisher.publishEvent(new RegistrationEvent(user, request.getLocale()));
		return user;
	}

	@ResponseStatus(CREATED)
	@PutMapping
	public User putUser(@RequestBody PutUserDto request) {
		return userService.putUser(request);
	}

	@DeleteMapping("/{id}")
	public void putUser(@PathVariable @NotNull UUID id) {
		userService.deleteUserById(id);
	}

}
