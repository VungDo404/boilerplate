INSERT INTO user (id,
				  display_name,
				  username,
				  password,
				  email,
				  image,
				  email_specify,
				  enabled,
				  account_non_locked,
				  credentials_non_expired,
				  account_non_expired,
				  is_two_factor_enabled,
				  security_stamp,
				  is_lockout_enabled,
				  access_failed_count,
				  lockout_end_date)
VALUES (UUID_TO_BIN(UUID()), -- id
		'Admin', -- display_name
		'admin', -- username
		'$2a$10$wk.ryX6se3Ca0Caua/ObWua/XJP.sZHFLON34hoMnLt03P83C6HvO', -- password
		'admin@example.com', -- email
		NULL, -- image
		NOW(), -- email_specify
		1, -- enabled
		1, -- account_non_locked
		1, -- credentials_non_expired
		1, -- account_non_expired
		0, -- is_two_factor_enabled
		NULL, -- security_stamp
		0, -- is_lockout_enabled
		10, -- access_failed_count
		NULL -- lockout_end_date
	   ),
	   (UUID_TO_BIN(UUID()), -- id
		'User Display', -- display_name
		'user', -- username
		'$2a$10$wk.ryX6se3Ca0Caua/ObWua/XJP.sZHFLON34hoMnLt03P83C6HvO', -- password
		'user@example.com', -- email
		NULL, -- image
		NOW(), -- email_specify
		1, -- enabled
		1, -- account_non_locked
		1, -- credentials_non_expired
		1, -- account_non_expired
		0, -- is_two_factor_enabled
		NULL, -- security_stamp
		0, -- is_lockout_enabled
		10, -- access_failed_count
		NULL -- lockout_end_date
	   );
