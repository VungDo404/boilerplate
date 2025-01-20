INSERT INTO acl_class(class)
VALUES
	('com.app.boilerplate.Domain.User');
INSERT INTO acl_sid(principal, sid, priority)
VALUES
	(0, 'ROLE_APPLICATION_ADMINISTRATOR', 100),
	(0, 'ROLE_AUTHENTICATE', 20),
	(0, 'ROLE_ANONYMOUS', 0),
	(1, 'LOCAL:admin', 500),
	(1, 'LOCAL:user', 20),
	(1, 'LOCAL:john@gmail.com', 20),
	(1, 'LOCAL:bob@gmail.com', 20);
