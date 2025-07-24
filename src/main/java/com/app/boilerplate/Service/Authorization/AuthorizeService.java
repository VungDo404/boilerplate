package com.app.boilerplate.Service.Authorization;

import com.app.boilerplate.Domain.Authorization.*;
import com.app.boilerplate.Repository.*;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Authorization.Dto.CreateAuthorityDto;
import com.app.boilerplate.Shared.Authorization.Dto.CreateObjectIdentityDto;
import com.app.boilerplate.Shared.Authorization.Event.AuthorityAfterRegisterEvent;
import com.app.boilerplate.Shared.Authorization.Event.ObjectIdentityEvent;
import com.app.boilerplate.Shared.Authorization.Model.AuthorityModel;
import com.app.boilerplate.Shared.Common.Identifiable;
import com.app.boilerplate.Shared.Common.IdentifiableUserResource;
import com.app.boilerplate.Util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
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
    private final AccessControlListService accessControlListService;
    private final JdbcTemplate jdbcTemplate;

    private final String INSERT_OBJECT_IDENTITY_WITH_OWNER_SID_LOOKUP = """
        INSERT INTO acl_object_identity
                (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
        SELECT
            ac.id,
            ?,
            (SELECT id FROM acl_object_identity WHERE object_id_identity = '0' AND object_id_class = ac.id),
            (SELECT id FROM acl_sid WHERE principal = true AND sid = ?),
            true
        FROM acl_class ac
        WHERE class = ?
        """;
    private final String INSERT_OBJECT_IDENTITY_WITH_PROVIDED_OWNER_ID = """
        INSERT INTO acl_object_identity
                (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
        SELECT
            ac.id,
            ?,
            (SELECT id FROM acl_object_identity WHERE object_id_identity = '0' AND object_id_class = ac.id),
            ?,
            true
        FROM acl_class ac
        WHERE class = ?
        """;
    private final String INSERT_ACL_ENTRY = """
        INSERT INTO acl_entry
                (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        SELECT
            ?,
            ?,
            (SELECT id FROM acl_sid WHERE principal = true AND sid = ?),
            ?,
            true,
            true,
            true
        """;

    private final String INSERT_ACL_SID = """
        INSERT IGNORE INTO acl_sid(principal, sid)
        VALUES (?,?)
        """;

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

    public <T extends Identifiable<?>> void createAcl(
        boolean isOwner,
        String sid,
        boolean principal,
        List<T> entities,
        List<Integer> masks) {

        final List<Object[]> entryParams = new ArrayList<>();
        final var className = entities.get(0).getClass().getName();
        final var sidId = accessControlListService.callCreateOrRetrieveSidPrimaryKey(sid, principal, true);
        final var ownerSid = isOwner ? sid : null;

        final List<Long> aclObjectIds = new ArrayList<>();

        for (T entity : entities) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                final var ps = connection.prepareStatement(
                    this.INSERT_OBJECT_IDENTITY_WITH_PROVIDED_OWNER_ID,
                    Statement.RETURN_GENERATED_KEYS
                                                          );
                ps.setString(1, entity.getId().toString());
                ps.setString(2, ownerSid);
                ps.setString(3, className);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                throw new IllegalStateException("Failed to retrieve ID from acl_object_identity insert");
            }
            aclObjectIds.add(keyHolder.getKey().longValue());
        }

        for (int ei = 0; ei < entities.size(); ei++) {
            final var currentId = aclObjectIds.get(ei);
            for (int mi = 0; mi < masks.size(); mi++) {
                final var mask = masks.get(mi);
                if (sid != null) {
                    entryParams.add(new Object[]{
                        currentId,
                        mi + 1,
                        sidId,
                        mask
                    });
                }
            }
        }

        if (!entryParams.isEmpty()) {
            jdbcTemplate.batchUpdate(this.INSERT_ACL_ENTRY, entryParams);
        }
    }

    public <T extends IdentifiableUserResource<?>> void createAcl(
        boolean isOwner,
        List<T> entities,
        List<Integer> masks) throws SQLException {

        final List<Object[]> sidParams = new ArrayList<>();
        final List<Long> aclObjectIds = new ArrayList<>();
        final List<Object[]> entryParams = new ArrayList<>();

        final var className = entities.get(0).getClass().getName();

        entities.forEach(entity -> {
            sidParams.add(new Object[]{
                true,
                entity.getUser().getSid()
            });
        });
        jdbcTemplate.batchUpdate(this.INSERT_ACL_SID, sidParams);

        for (T entity : entities) {
            final var sid = isOwner ? entity.getUser().getSid() : null;
            final KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                final var ps = connection.prepareStatement(
                    this.INSERT_OBJECT_IDENTITY_WITH_OWNER_SID_LOOKUP,
                    Statement.RETURN_GENERATED_KEYS
                                                          );
                ps.setString(1, entity.getId().toString());
                ps.setString(2, sid);
                ps.setString(3, className);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                throw new SQLException("Failed to retrieve generated ID for acl_object_identity");
            }
            aclObjectIds.add(keyHolder.getKey().longValue());
        }

        for (int ei = 0; ei < entities.size(); ei++) {
            final var entity = entities.get(ei);
            final var aclObjectId = aclObjectIds.get(ei);

            for (int mi = 0; mi < masks.size(); mi++) {
                final var mask = masks.get(mi);
                entryParams.add(new Object[]{
                    aclObjectId,
                    mi + 1,
                    entity.getUser().getSid(),
                    mask
                });
            }
        }

        if (!entryParams.isEmpty()) {
            jdbcTemplate.batchUpdate(this.INSERT_ACL_ENTRY, entryParams);
        }
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
        if (SecurityUtil.isAnonymous()) {
            final var authorities = SecurityUtil.getAuthorities();
            final var authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
            rawResults = authorityRepository.findAclEntriesBySid(authorityStrings);
        } else {
            rawResults = authorityRepository.findAclEntriesByUserId(id, SecurityUtil.getPrincipalSid()
                .getPrincipal());
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

        if (createObjectIdentityDto.getOwnerSid() != null) {
            final var ownerSid = aclSidRepository.findById(createObjectIdentityDto.getOwnerSid())
                .orElseThrow(() -> new EntityNotFoundException("Owner SID not found"));
            objectIdentity.setOwnerSid(ownerSid);
        }

        objectIdentity.setEntriesInheriting(createObjectIdentityDto.isEntriesInheriting());

        final var returnedObjectIdentity = aclObjectIdentityRepository.save(objectIdentity);

        return returnedObjectIdentity.getId();
    }

    @EventListener
    public void addAuthority(AuthorityAfterRegisterEvent event) {
        final var id = event.getUser()
            .getId()
            .toString();
        authorityRepository.insertAuthoritiesAfterRegister(id);
    }

    @EventListener
    public void addObjectIdentity(ObjectIdentityEvent<?> event) {
        final var entity = event.getEntity();
        final var className = entity.getClass()
            .getName();
        final var id = entity.getId()
            .toString();
        final var sid = SecurityUtil.isAnonymous() ?
            null :
            accessControlListService.callCreateOrRetrieveSidPrimaryKey(
                SecurityUtil.getPrincipalSid()
                    .getPrincipal(),
                true,
                true);
        aclObjectIdentityRepository.createAclObjectIdentity(className, id, sid);
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

    private Long getParentAclId(Long classId, String parentObjectId) {
        return jdbcTemplate.queryForObject("""
                SELECT id FROM acl_object_identity
                WHERE object_id_class = ? AND object_id_identity = ?
            """, Long.class, classId, parentObjectId);
    }
}
