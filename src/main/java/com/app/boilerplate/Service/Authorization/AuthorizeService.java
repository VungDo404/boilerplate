package com.app.boilerplate.Service.Authorization;

import com.app.boilerplate.Domain.Authorization.*;
import com.app.boilerplate.Repository.*;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Authorization.Dto.CreateAuthorityDto;
import com.app.boilerplate.Shared.Authorization.Dto.CreateObjectIdentityDto;
import com.app.boilerplate.Shared.Authorization.Model.AuthorityModel;
import com.app.boilerplate.Util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    public List<AclSid> getSidByPrincipal(boolean principal) {
        return aclSidRepository.findByPrincipal(principal);
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

    public Collection<GrantedAuthority> getSidGrantedAuthorities(String id) {
        try {
            final var sids = authorityRepository.findSidsByUserId(id);
            return sids.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<AuthorityModel> getGrantedAuthority(String id) {
        final List<Object[]> rawResults;
        if(SecurityUtil.isAnonymous()){
            final var authorities = SecurityUtil.getAuthorities();
            final var authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
            rawResults = authorityRepository.findAclEntriesBySid(authorityStrings);
        }else{
            rawResults = authorityRepository.findAclEntriesByUserId(id, SecurityUtil.getPrincipalSid().getPrincipal());
        }

        return rawResults.stream()
            .map(entry -> new AuthorityModel(
                ((Number) entry[0]).intValue(),
                (String) entry[1],
                ((Number) entry[2]).intValue() == 1,
                entry[3].toString()
            ))
            .collect(Collectors.toList());
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

    public Long addObjectIdentity(CreateObjectIdentityDto createObjectIdentityDto) {
        final var objectIdentity = new AclObjectIdentity();

        final var aclClass = aclClassRepository.findById(createObjectIdentityDto.getObjectIdClass())
            .orElseThrow(() -> new EntityNotFoundException("AclClass not found"));
        objectIdentity.setObjectIdClass(aclClass);

        objectIdentity.setObjectIdIdentity(createObjectIdentityDto.getObjectIdIdentity());

        if (createObjectIdentityDto.getParentObject() != null) {
            final var parentObject = aclObjectIdentityRepository
                .findById(createObjectIdentityDto.getParentObject())
                .orElseThrow(() -> new EntityNotFoundException("Parent object not found"));
            checkForCircularDependency(parentObject);
            objectIdentity.setParentObject(parentObject);
        }

        final var ownerSid = aclSidRepository.findById(createObjectIdentityDto.getOwnerSid())
            .orElseThrow(() -> new EntityNotFoundException("Owner SID not found"));
        objectIdentity.setOwnerSid(ownerSid);

        objectIdentity.setEntriesInheriting(createObjectIdentityDto.isEntriesInheriting());

        final var returnedObjectIdentity = aclObjectIdentityRepository.save(objectIdentity);

        return returnedObjectIdentity.getId();
    }

    public Page<Authority> getAuthorities(Pageable pageable) {
        return authorityRepository.findAll(pageable);
    }

    private void checkForCircularDependency(AclObjectIdentity parentObject) {
        var slow = parentObject;
        var fast = parentObject.getParentObject();
        while (fast != null && fast.getParentObject() != null) {
            slow = slow.getParentObject();
            fast = fast.getParentObject()
                .getParentObject();
            if (slow == fast) {
                throw new IllegalStateException("Circular dependency detected in ACL object hierarchy");
            }
        }
    }
}
