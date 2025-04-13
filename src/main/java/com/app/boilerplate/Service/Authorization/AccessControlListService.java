package com.app.boilerplate.Service.Authorization;

import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Util.SecurityUtil;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Optional;

@Transactional
@Service
public class AccessControlListService extends JdbcMutableAclService {

    public AccessControlListService(
        final DataSource dataSource,
        final LookupStrategy lookupStrategy,
        final AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }

    public void addPermission(Class<?> type, Long objectId, Permission permission) {
        addPermission(type, objectId, permission, SecurityUtil.getPrincipalSid()
            .getPrincipal(), true);
    }

    public void addPermission(Class<?> type, Long objectId, Permission permission, String sidName, boolean principal) {
        performAclOperation(type, objectId, acl -> {
            final var sid = principal ? new PrincipalSid(sidName) : new GrantedAuthoritySid(sidName);
            acl.insertAce(acl.getEntries()
                .size(), permission, sid, true);
        });
    }

    public void addAdminPermission(Class<?> type, Long objectId) {
        addPermission(type, objectId, HierarchicalPermission.ADMINISTRATION);
    }

    public void removePermission(Class<?> type, Long objectId, Permission permission) {
        removePermission(type, objectId, permission, SecurityUtil.getPrincipalSid()
            .getPrincipal(), true);
    }

    public void removePermission(
        Class<?> type, Long objectId, Permission permission, String sidName,
        boolean principal) {
        performAclOperation(type, objectId, acl -> {
            final var sid = principal ? new PrincipalSid(sidName) : new GrantedAuthoritySid(sidName);
            for (int i = 0; i < acl.getEntries()
                .size(); i++) {
                final var entry = acl.getEntries()
                    .get(i);
                if (entry.getSid()
                    .equals(sid) && entry.getPermission()
                    .equals(permission)) {
                    acl.deleteAce(i);
                    return;
                }
            }
        });
    }

    public void togglePermission(
        Class<?> type, Long objectId, Permission permission, String sidName,
        boolean principal) {
        performAclOperation(type, objectId, acl -> {
            final var sid = principal ? new PrincipalSid(sidName) : new GrantedAuthoritySid(sidName);
            for (int i = 0; i < acl.getEntries()
                .size(); i++) {
                final var entry = acl.getEntries()
                    .get(i);
                if (entry.getSid()
                    .equals(sid) && entry.getPermission()
                    .equals(permission)) {
                    acl.deleteAce(i);
                    acl.insertAce(i, permission, sid, !entry.isGranting());
                    return;
                }
            }
            acl.insertAce(acl.getEntries()
                .size(), permission, sid, true);
        });
    }

    public void togglePermission(Class<?> type, Long objectId, Permission permission) {
        togglePermission(type, objectId, permission, SecurityUtil.getPrincipalSid()
            .getPrincipal(), true);
    }

    public Long callCreateOrRetrieveClass(String type, boolean allowCreate, Class idType) {
        return createOrRetrieveClassPrimaryKey(type, allowCreate, idType);
    }

    public Long callCreateOrRetrieveSidPrimaryKey(String sidName, boolean sidIsPrincipal, boolean allowCreate) {
        return createOrRetrieveSidPrimaryKey(sidName, sidIsPrincipal, allowCreate);
    }

    @Override
    public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
        return createAcl(objectIdentity, SecurityUtil.getPrincipalSid()
            .getPrincipal(), true);
    }

    public MutableAcl createAcl(
        ObjectIdentity objectIdentity, String sidName,
        boolean principal) {
        Assert.notNull(objectIdentity, "Object Identity required");

        if (this.retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
            throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
        } else {
            final var sid = principal ? new PrincipalSid(sidName) : new GrantedAuthoritySid(sidName);
            createObjectIdentity(objectIdentity, sid);
            final var acl = (MutableAcl) this.readAclById(objectIdentity);
            Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should have been returned");
            return acl;
        }
    }

    private void performAclOperation(Class<?> type, Long objectId, AclOperation operation) {
        final var objectIdentity = new ObjectIdentityImpl(type, objectId);
        final var acl = Optional.of(objectIdentity)
            .map(super::readAclById)
            .map(MutableAcl.class::cast)
            .orElseGet(() -> createAcl(objectIdentity));
        ;

        operation.execute(acl);
        updateAcl(acl);
    }

    @FunctionalInterface
    private interface AclOperation {
        void execute(MutableAcl acl);
    }
}
