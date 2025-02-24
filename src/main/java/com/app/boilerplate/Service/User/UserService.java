package com.app.boilerplate.Service.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Repository.UserRepository;
import com.app.boilerplate.Shared.Account.Event.EmailActivationEvent;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.PutUserDto;
import com.app.boilerplate.Shared.User.Dto.UserCriteriaDto;
import com.app.boilerplate.Util.RandomUtil;
import com.app.boilerplate.Util.Translator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService implements Translator {
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	private final RandomUtil randomUtil;
	private final IUserMapper userMapper;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(readOnly = true)
	public Page<User> getAllUsers(Optional<UserCriteriaDto> userCriteriaDto, Pageable pageable) {
		final var specification = UserSpecification.specification(userCriteriaDto);
		return userRepository.findAll(specification, pageable);
	}

	@Cacheable(value = "user", key = "#id")
	@Transactional(readOnly = true)
	public User getUserById(UUID id) {
		return Optional.of(id)
			.flatMap(userRepository::findById)
			.orElseThrow(() -> new NotFoundException(
				translateEnglish("error.user.id.notfound", id),
				"error.user.id.notfound", id));
	}

	@Transactional(readOnly = true)
	public User getUserByUsername(String username) {
		return Optional.of(username)
			.flatMap(userRepository::findOneByUsername)
			.orElseThrow(() -> new UsernameNotFoundException(
				translateEnglish("error.user.login.notfound", username)));
	}

	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return Optional.of(email)
			.flatMap(userRepository::findOneByEmailIgnoreCase)
			.orElseThrow(
				() -> new NotFoundException(
					translateEnglish("error.user.email.notfound", email),
					"error.user.email.notfound", email));
	}

	@CachePut(value = "user", key = "#result.id")
	public User createUser(CreateUserDto request, Boolean shouldSendConfirmationEmail) {
		Optional.of(request.getEmail())
			.filter(userRepository::existsByEmailIgnoreCase)
			.ifPresent(email -> {
				throw new AlreadyExistsException("error.email.exist");
			});
		final var u = Optional.of(request)
			.filter(PostUserDto.class::isInstance)
			.map(PostUserDto.class::cast)
			.map(userMapper::toUser)
			.orElseGet(() -> userMapper.toUser(request));
		final var user = Optional.of(u)
			.map(req -> {
				req.setPassword(Optional.ofNullable(req.getPassword())
					.orElseGet(randomUtil::randomPassword));
				req.setSecurityStamp(UUID.randomUUID()
					.toString());
				req.setProvider(LoginProvider.LOCAL);
				return req;
			})
			.map(this::save)
			.orElseThrow();
		if (shouldSendConfirmationEmail)
			eventPublisher.publishEvent(new EmailActivationEvent(user));
		return user;
	}

	@CachePut(value = "user", key = "#request.id")
	public User putUser(PutUserDto request) {
		return Optional.of(request.getId())
			.map(this::getUserById)
			.map(user -> {
				userMapper.update(user, request);
				user.setSecurityStamp(UUID.randomUUID()
					.toString());
				log.debug("Updated Information for User: {}", user);
				return save(user);
			})
			.orElseThrow(() -> new NotFoundException(
				translateEnglish("error.user.email.notfound", request.getId()),
				"error.user.id.notfound", request.getId()));
	}

	@CacheEvict(value = "user", key = "#id")
	public void deleteUserById(UUID id) {
		userRepository.deleteById(id);
	}

	@CachePut(value = "user", key = "#user.id")
	public User save(User user) {
		return userRepository.save(user);
	}

}
