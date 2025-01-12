INSERT INTO acl_class(class)
VALUES
	('com.app.boilerplate.Domain.User');
INSERT INTO acl_sid(principal, sid)
VALUES
	(0, 'ROLE_APPLICATION_ADMINISTRATOR'),
	(0, 'ROLE_AUTHENTICATE'),
	(0, 'ROLE_ANONYMOUS'),
	(1, 'LOCAL:admin'),
	(1, 'LOCAL:user'),
	(1, 'LOCAL:john@gmail.com'),
	(1, 'LOCAL:bob@gmail.com');
