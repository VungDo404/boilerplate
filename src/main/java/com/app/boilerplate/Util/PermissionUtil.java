package com.app.boilerplate.Util;

import org.springframework.stereotype.Component;

@Component
public final class PermissionUtil {
	public static final String USER = "com.app.boilerplate.Domain.User";
	public static final String APPLICATION = "com.app.boilerplate.Domain.Application";
	public static final String AUTHENTICATION = "com.app.boilerplate.Domain.Authentication";
	public static final String AUTHORIZATION = "com.app.boilerplate.Domain.Authorization";
	public static final String DOMAIN = "com.app.boilerplate.Domain";
	public static final String ACCOUNT = "com.app.boilerplate.Domain.Account";

	public static final String READ = "READ";
	public static final String WRITE = "WRITE";
	public static final String CREATE = "CREATE";
	public static final String DELETE = "DELETE";
	public static final String GRANT = "GRANT";
	public static final String ADMIN = "ADMINISTRATION";

	public static String hasPermission(String targetId, String targetType, String permission) {
		return "hasPermission('" + targetId + "', '" + targetType + "', '" + permission + "')";
	}

	public static String hasPermission(String targetType, String permission) {
		return hasPermission("0", targetType, permission);
	}

	private PermissionUtil() {}
}
