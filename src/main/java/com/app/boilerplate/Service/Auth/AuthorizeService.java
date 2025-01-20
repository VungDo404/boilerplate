package com.app.boilerplate.Service.Auth;

import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Util.SecurityUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;

@Transactional
@Service
public class AuthorizeService extends JdbcMutableAclService {
	private final SecurityUtil securityUtil;
	private final JdbcTemplate jdbcTemplate;

	public AuthorizeService(final DataSource dataSource, final LookupStrategy lookupStrategy, final AclCache aclCache,
							final SecurityUtil securityUtil, final JdbcTemplate jdbcTemplate) {
		super(dataSource, lookupStrategy, aclCache);
		this.securityUtil = securityUtil;
		this.jdbcTemplate = jdbcTemplate;
	}

	public void addPermission(Class<?> type, Long objectId, Permission permission) {
		performAclOperation(type, objectId, acl -> {
			final var sid = securityUtil.getPrincipalSid();
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		});
	}

	public void addAdminPermission(Class<?> type, Long objectId) {
		addPermission(type, objectId, HierarchicalPermission.ADMINISTRATION);
	}

	public void removePermission(Class<?> type, Long objectId, Permission permission) {
		performAclOperation(type, objectId, acl -> {
			final var sid = securityUtil.getPrincipalSid();
			for (int i = 0; i < acl.getEntries().size(); i++) {
				final var entry = acl.getEntries().get(i);
				if (entry.getSid().equals(sid) && entry.getPermission().equals(permission)) {
					acl.deleteAce(i);
					return;
				}
			}
		});
	}

	public void togglePermission(Class<?> type, Long objectId, Permission permission) {
		performAclOperation(type, objectId, acl -> {
			final var sid = securityUtil.getPrincipalSid();
			for (int i = 0; i < acl.getEntries().size(); i++) {
				final var entry = acl.getEntries().get(i);
				if (entry.getSid().equals(sid) && entry.getPermission().equals(permission)) {
					acl.deleteAce(i);
					acl.insertAce(i, permission, sid, !entry.isGranting());
					return;
				}
			}
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		});
	}

	@Override
	public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
		Assert.notNull(objectIdentity, "Object Identity required");

		if (this.retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
			throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
		} else {
			// Use custom logic to retrieve PrincipalSid
			PrincipalSid sid = securityUtil.getPrincipalSid();
			this.createObjectIdentity(objectIdentity, sid);
			Acl acl = this.readAclById(objectIdentity);
			Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should have been returned");
			return (MutableAcl) acl;
		}
	}

	private void performAclOperation(Class<?> type, Long objectId, AclOperation operation) {
		final var objectIdentity = new ObjectIdentityImpl(type, objectId);
		MutableAcl acl;
		try {
			// Try to read the existing ACL
			acl = (MutableAcl) readAclById(objectIdentity);
		} catch (NotFoundException e) {
			// If not found, create a new ACL
			acl = createAcl(objectIdentity);
		}
		operation.execute(acl);
		updateAcl(acl);
	}

	@FunctionalInterface
	private interface AclOperation {
		void execute(MutableAcl acl);
	}
}
