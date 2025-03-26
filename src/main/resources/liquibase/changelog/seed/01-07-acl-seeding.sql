INSERT INTO acl_class(class, class_id_type)
VALUES ('com.app.boilerplate.Domain.User', 'java.lang.String'),
       ('com.app.boilerplate.Domain.Application', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Authentication', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Authorization', 'java.lang.Long'),
       ('com.app.boilerplate.File', 'java.lang.Long'),
       ('com.app.boilerplate.Domain', 'java.lang.Long');
-- ROLE_RESOURCE_PERMISSION
INSERT INTO acl_sid(principal, sid)
VALUES (0, 'ROLE_APPLICATION_ADMINISTRATOR'),
       (0, 'ROLE_AUTHENTICATE'),
       (0, 'ROLE_AUTHENTICATION_ANONYMOUS'),
       (0, 'ROLE_USER_ADMINISTRATOR'),
       (0, 'ROLE_AUTHENTICATION_ADMINISTRATOR'),
       (0, 'ROLE_AUTHORIZATION_ADMINISTRATOR'),
       (0, 'ROLE_DOMAIN_ADMINISTRATOR');

INSERT INTO acl_sid(principal, sid)
SELECT 1, CONCAT('LOCAL:', username)
FROM user;

SET
@role_admin_app_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_APPLICATION_ADMINISTRATOR'
						AND principal = 0);
SET
@role_user_app_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_USER_ADMINISTRATOR'
						AND principal = 0);
SET
@role_authen_app_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_AUTHENTICATION_ADMINISTRATOR'
						AND principal = 0);
SET
@role_author_app_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_AUTHORIZATION_ADMINISTRATOR'
						AND principal = 0);
SET
@role_domain_app_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_DOMAIN_ADMINISTRATOR'
						AND principal = 0);
SET
@role_anonymous = (SELECT id
				  FROM acl_sid
				  WHERE sid = 'ROLE_AUTHENTICATION_ANONYMOUS'
					AND principal = 0);
SET
@read = 1 << 0;
SET
@write = 1 << 1;
SET
@create = 1 << 2;
SET
@delete = 1 << 3;
SET
@grant = 1 << 4;
SET
@admin = 1 << 5;

SET
@acl_class_user_id = (
    SELECT id
    FROM acl_class
    WHERE class = 'com.app.boilerplate.Domain.User'
);

INSERT INTO acl_object_identity(object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT acl_class.id, 0, NULL, @role_admin_app_id, 1
FROM acl_class
WHERE NOT EXISTS(SELECT 1
                 FROM acl_object_identity
                 WHERE acl_class.id = acl_object_identity.object_id_class
                   AND acl_object_identity.object_id_identity = 0);

SET
@application_obj_id = (
    SELECT id
    FROM acl_object_identity
    WHERE object_id_class = (
    		SELECT id
    		FROM acl_class
    		WHERE class = 'com.app.boilerplate.Domain.Application'
    	)
    	AND object_id_identity = 0
);
SET
@user_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.User'
             )
        	AND object_id_identity = 0
);

SET
@authentication_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.Authentication')
        	AND object_id_identity = 0
);
SET
@authorization_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.Authorization')
        	AND object_id_identity = 0
);
SET
@domain_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain')
        	AND object_id_identity = 0
);

UPDATE acl_object_identity
SET parent_object = @application_obj_id
WHERE entries_inheriting = 1
  AND id <> @application_obj_id
  AND object_id_identity = '0';

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT @acl_class_user_id,
       BIN_TO_UUID(u.id),
       @user_obj_id,
       (SELECT id FROM acl_sid WHERE sid = CONCAT('LOCAL:', u.username) LIMIT 1),
        1
FROM user u;

INSERT INTO acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (@application_obj_id, 1, @role_admin_app_id, @admin, 1, 1, 1),
       (@user_obj_id, 1, @role_user_app_id, @admin, 1, 1, 1),
       (@authentication_obj_id, 1, @role_authen_app_id, @admin, 1, 1, 1),
       (@authentication_obj_id, 2, @role_anonymous, @read, 1, 1, 1),
       (@authentication_obj_id, 3, @role_anonymous, @create, 1, 1, 1),
       (@authentication_obj_id, 4, @role_anonymous, @write, 1, 1, 1),
       (@authorization_obj_id, 1, @role_author_app_id, @admin, 1, 1, 1),
       (@domain_obj_id, 1, @role_domain_app_id, @admin, 1, 1, 1);
