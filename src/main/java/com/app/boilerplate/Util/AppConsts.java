package com.app.boilerplate.Util;

import java.util.UUID;

public final class AppConsts {

	public static final String TOKEN_TYPE = "typ";
	public static final String SECURITY_STAMP = "sta";
	public static final String JWT_SUBJECT = "sub"; //id
	public static final String JWT_JTI = "jti";
	public static final String REFRESH_TOKEN_ID = "rti";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String ACCESS_TOKEN = "at";
	public static final String ACCESS_TOKEN_PROVIDER = "pvd";
	public static final String ACCESS_TOKEN_USERNAME = "un";
	public static final String ACCESS_TOKEN_ID = "ati";

	public static final String HMAC_SHA_256 = "HmacSHA256";
	public static final String HMAC_SHA_1 = "HmacSHA1";

	public static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public static final String ANONYMOUS_USER = "anonymousUser";

}
