package com.app.boilerplate.Security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public class HierarchicalPermission extends BasePermission {
	public static final int MASK_READ = 1 << 0;
	public static final int MASK_WRITE = 1 << 1;
	public static final int MASK_CREATE = 1 << 2;
	public static final int MASK_DELETE = 1 << 3;
	public static final int MASK_GRANT = 1 << 4;
	public static final int MASK_ADMINISTRATION = 1 << 5;
	public static final int MASK_VIEW = 1 << 6;

	public static final Permission GRANT = new HierarchicalPermission(MASK_GRANT, 'G');
	public static final Permission ADMINISTRATION = new HierarchicalPermission(MASK_ADMINISTRATION, 'A');
	public static final Permission VIEW = new HierarchicalPermission(MASK_VIEW, 'V');

	protected HierarchicalPermission(int mask) {
		super(mask);
	}
	protected HierarchicalPermission(final int mask, final char code) {
		super(mask, code);
	}
}
