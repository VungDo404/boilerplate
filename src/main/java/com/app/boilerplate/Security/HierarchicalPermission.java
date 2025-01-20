package com.app.boilerplate.Security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public class HierarchicalPermission extends BasePermission {
	public static final Permission GRANT = new HierarchicalPermission(1 << 4, 'G');
	public static final Permission ADMINISTRATION = new HierarchicalPermission(1 << 5, 'A');

	protected HierarchicalPermission(int mask) {
		super(mask);
	}
	protected HierarchicalPermission(final int mask, final char code) {
		super(mask, code);
	}
}
