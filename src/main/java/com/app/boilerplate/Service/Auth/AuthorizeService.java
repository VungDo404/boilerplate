package com.app.boilerplate.Service.Auth;

import com.app.boilerplate.Domain.Authorization.*;
import com.app.boilerplate.Repository.*;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Authorization.Dto.CreateAuthorityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthorizeService {
	private final AclClassRepository aclClassRepository;
	private final AclEntryRepository aclEntryRepository;
	private final AclObjectIdentityRepository aclObjectIdentityRepository;
	private final AclSidRepository aclSidRepository;
	private final AuthorityRepository authorityRepository;
	private final UserService userService;

	public Page<AclSid> getSid(Pageable pageable) {
		return aclSidRepository.findAll(pageable);
	}

	public Page<AclClass> getClasses(Pageable pageable) {
		return aclClassRepository.findAll(pageable);
	}

	public Page<AclObjectIdentity> getObjects(Pageable pageable) {
		return aclObjectIdentityRepository.findAll(pageable);
	}

	public Page<AclEntry> getEntries(Pageable pageable) {
		return aclEntryRepository.findAll(pageable);
	}

	public Page<AclEntry> getEntries(Pageable pageable, UUID userId) {
		return aclEntryRepository.findByUserId(pageable, userId);
	}

	public Page<AclEntry> getEntries(Pageable pageable, Long sid) {
		return aclEntryRepository.findBySid(pageable, sid);
	}

	public Collection<GrantedAuthority> getGrantedAuthorities(String id) {
		try {
			List<String> sids = authorityRepository.findSidsByUserId(id);
			return sids.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public Authority grantedAuthority(CreateAuthorityDto createAuthorityDto) {
		final var authority = new Authority();
		AuthorityId authorityId = new AuthorityId();

		authorityId.setUserId(createAuthorityDto.getUserId());
		authorityId.setSidId(createAuthorityDto.getSid());

		authority.setId(authorityId);
		authority.setPriority(createAuthorityDto.getPriority());

		final var user = userService.getUserById(createAuthorityDto.getUserId());
		final var sid = aclSidRepository.findById(createAuthorityDto.getSid())
			.orElseThrow(() -> new NotFoundException("AclSid not found"));

		authority.setUser(user);
		authority.setSid(sid);

		return authorityRepository.save(authority);
	}

	public Page<Authority> getAuthorities(Pageable pageable) {
		return authorityRepository.findAll(pageable);
	}
}
