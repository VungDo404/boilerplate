package com.app.boilerplate.Security;

import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class HierarchicalPermissionGrantingStrategy extends DefaultPermissionGrantingStrategy {
	private final transient AuditLogger auditLogger;

	public HierarchicalPermissionGrantingStrategy(final AuditLogger auditLogger) {
		super(auditLogger);
		this.auditLogger = auditLogger;
	}

	@Override
	public boolean isGranted(Acl acl, List<Permission> permissions, List<Sid> sids, boolean administrativeMode) throws NotFoundException {
		Assert.isTrue(permissions.size() == 1, "The permissions list must have exactly one element.");
		Permission permission = permissions.get(0);
		List<AccessControlEntry> aces = Optional.ofNullable(acl.getEntries())
			.orElse(Collections.emptyList())
			.stream()
			.filter(ace -> ace.getPermission()
				.getMask() == permission.getMask() || ace.getPermission()
				.getMask() == HierarchicalPermission.ADMINISTRATION.getMask())
			.toList();
		AccessControlEntry firstRejection = null;

		for (final Sid sid : sids) {
			boolean scanNextSid = true;

			for (final AccessControlEntry ace : aces) {
				if (this.isGranted(ace, permission) && ace.getSid()
					.equals(sid)) {
					if (ace.isGranting()) {
						if (!administrativeMode) {
							this.auditLogger.logIfNeeded(true, ace);
						}

						return true;
					}

					if (firstRejection == null) {
						firstRejection = ace;
					}

					scanNextSid = false;
					break;
				}
			}

			if (!scanNextSid) {
				break;
			}

		}

		if (firstRejection != null) {
			if (!administrativeMode) {
				this.auditLogger.logIfNeeded(false, firstRejection);
			}

			return false;
		} else if (acl.isEntriesInheriting() && acl.getParentAcl() != null) {
			return acl.getParentAcl()
				.isGranted(permissions, sids, false);
		} else {
			throw new NotFoundException("Unable to locate a matching ACE for passed permissions and SIDs");
		}
	}

	@Override
	protected boolean isGranted(AccessControlEntry ace, Permission p) {
		return ace.getPermission()
			.getMask() == p.getMask() || ace.getPermission()
			.getMask() == HierarchicalPermission.ADMINISTRATION.getMask();
	}

}
