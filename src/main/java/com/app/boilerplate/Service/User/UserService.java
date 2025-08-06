package com.app.boilerplate.Service.User;

import com.app.boilerplate.Decorator.AclAware.AclAware;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Repository.UserRepository;
import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Security.OAuth2UserInfo;
import com.app.boilerplate.Shared.Account.Event.EmailActivationEvent;
import com.app.boilerplate.Shared.Account.Event.RegisterUserEvent;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.UpdateUserDto;
import com.app.boilerplate.Shared.User.Dto.UserCriteriaDto;
import com.app.boilerplate.Util.RandomUtil;
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
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
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
            .orElseThrow(() -> new NotFoundException("", "error.user.id.notfound", id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameAndProvider(String username, LoginProvider loginProvider) {
        return Optional.of(username)
            .flatMap(un -> userRepository.findOneByUsernameAndProvider(username, loginProvider))
            .orElseThrow(() -> new UsernameNotFoundException("error.user.login.notfound"));
    }

    public User getOrCreateExternalUserIfNotExists(OAuth2UserInfo info, LoginProvider loginProvider) {
        try {
            return getUserByUsernameAndProvider(info.getId(), loginProvider);
        } catch (UsernameNotFoundException e) {
            final var dto = userMapper.toPostUserDto(info);
            return createUser(dto, false, loginProvider);
        }
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return Optional.of(email)
            .flatMap(userRepository::findOneByEmailIgnoreCase)
            .orElseThrow(
                () -> new NotFoundException("", "error.user.email.notfound", email));
    }

    @AclAware(permissions = {HierarchicalPermission.MASK_READ, HierarchicalPermission.MASK_WRITE})
    @CachePut(value = "user", key = "#result.id")
    public User createUser(CreateUserDto request, Boolean shouldSendConfirmationEmail, LoginProvider loginProvider) {
        if (loginProvider.equals(LoginProvider.LOCAL))
            Optional.of(request.getEmail())
                .filter(mail -> userRepository.existsByEmailIgnoreCaseAndProvider(mail, loginProvider))
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
                    .orElseGet(RandomUtil::randomPassword));
                req.setSecurityStamp(UUID.randomUUID()
                    .toString());
                req.setProvider(loginProvider);
                return req;
            })
            .map(this::save)
            .orElseThrow();
        if (shouldSendConfirmationEmail)
            eventPublisher.publishEvent(new EmailActivationEvent(user));
        eventPublisher.publishEvent(new RegisterUserEvent(user));

        return user;
    }

    @Transactional
    @CachePut(value = "user", key = "#id")
    public User putUser(UpdateUserDto request, UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("", "error.user.id.notfound", id));

        log.debug("Before update: {}", user);
        userMapper.update(user, request);
        user.setSecurityStamp(UUID.randomUUID()
            .toString());
        log.debug("After update: {}", user);

        return userRepository.saveAndFlush(user);
    }

    @CacheEvict(value = "user", key = "#id")
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    @CachePut(value = "user", key = "#user.id")
    public User save(User user) {
        return userRepository.saveAndFlush(user);
    }

}
