INSERT INTO acl_class(class, class_id_type)
VALUES ('com.app.boilerplate.Domain.User.User', 'java.lang.String'),
       ('com.app.boilerplate.Domain.Application', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Authentication', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Authorization', 'java.lang.Long'),
       ('com.app.boilerplate.File', 'java.lang.Long'),
       ('com.app.boilerplate.Domain', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Notification.Notification', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Notification.NotificationTopic', 'java.lang.Long'),
       ('com.app.boilerplate.Domain.Notification.TopicSubscription', 'java.lang.String'),
       ('com.app.boilerplate.Domain.Notification.NotificationUser', 'java.lang.String');
-- ROLE_RESOURCE_PERMISSION
INSERT INTO acl_sid (principal, sid)
SELECT DISTINCT 0, CONCAT('ROLE_', UPPER(SUBSTRING_INDEX(class, '.', -1)), '_ADMINISTRATOR')
FROM acl_class
WHERE CONCAT('ROLE_', UPPER(SUBSTRING_INDEX(class, '.', -1)), '_ADMINISTRATOR')
          NOT IN (SELECT sid FROM acl_sid);
INSERT INTO acl_sid(principal, sid)
SELECT 1, CONCAT('LOCAL:', username)
FROM user;

INSERT INTO acl_sid(principal, sid)
VALUES (0, 'ROLE_AUTHENTICATION_ANONYMOUS'),
       (0, 'ROLE_AUTHENTICATE');

SET
@role_admin_app_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_APPLICATION_ADMINISTRATOR'
						AND principal = 0);
SET
@role_authen_id = (SELECT id
					  FROM acl_sid
					  WHERE sid = 'ROLE_AUTHENTICATE'
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
@view = 1 << 6;
SET
@acl_class_user_id = (
    SELECT id
    FROM acl_class
    WHERE class = 'com.app.boilerplate.Domain.User.User'
);

SET
@acl_class_noti_id = (
    SELECT id
    FROM acl_class
    WHERE class = 'com.app.boilerplate.Domain.Notification.Notification'
);

SET
@acl_class_noti_topic_id = (
    SELECT id
    FROM acl_class
    WHERE class = 'com.app.boilerplate.Domain.Notification.NotificationTopic'
);

SET
@acl_class_topic_sub_id = (
    SELECT id
    FROM acl_class
    WHERE class = 'com.app.boilerplate.Domain.Notification.TopicSubscription'
);

SET
@acl_class_noti_user_id = (
    SELECT id
    FROM acl_class
    WHERE class = 'com.app.boilerplate.Domain.Notification.NotificationUser'
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
    			WHERE class = 'com.app.boilerplate.Domain.User.User'
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
@noti_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.Notification.Notification')
        	AND object_id_identity = 0
);

SET
@noti_topic_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.Notification.NotificationTopic')
        	AND object_id_identity = 0
);

SET
@topic_sub_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.Notification.TopicSubscription')
        	AND object_id_identity = 0
);

SET
@noti_user_obj_id = (
         SELECT id
         FROM acl_object_identity
         WHERE object_id_class = (
         		SELECT id
    			FROM acl_class
    			WHERE class = 'com.app.boilerplate.Domain.Notification.NotificationUser')
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

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT @acl_class_noti_id,
       id,
       @noti_obj_id,
       @role_admin_app_id,
       1
FROM notification;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT @acl_class_noti_topic_id,
       id,
       @noti_topic_obj_id,
       @role_admin_app_id,
       1
FROM notification_topic;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT @acl_class_topic_sub_id,
       CONCAT(BIN_TO_UUID(user_id), ':', notification_topic_id),
       @topic_sub_obj_id,
       @role_admin_app_id,
       1
FROM topic_subscription;

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT @acl_class_noti_user_id,
       CONCAT(BIN_TO_UUID(user_id), ':', notification_id),
       @noti_user_obj_id,
       @role_admin_app_id,
       1
FROM notification_user;

INSERT INTO acl_entry (acl_object_identity,
                       ace_order,
                       sid,
                       mask,
                       granting,
                       audit_success,
                       audit_failure)
SELECT aoi.id AS acl_object_identity,
       1      AS ace_order,
       sid.id AS sid,
       @admin AS mask,
       TRUE   AS granting,
       TRUE   AS audit_success,
       TRUE   AS audit_failure
FROM acl_object_identity aoi
         JOIN acl_class ac ON aoi.object_id_class = ac.id
         JOIN acl_sid sid
              ON sid.sid = CONCAT('ROLE_', UPPER(SUBSTRING_INDEX(ac.class, '.', -1)), '_ADMINISTRATOR')
WHERE aoi.object_id_identity = '0';

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT aoi.id,
       perms.ace_order,
       sid.id,
       perms.mask,
       1,
       1,
       1
FROM notification_user nu
         JOIN user u ON u.id = nu.user_id
         JOIN acl_sid sid ON sid.principal = 1 AND sid.sid = CONCAT('LOCAL:', u.username)
         JOIN acl_object_identity aoi
              ON aoi.object_id_identity = CONCAT(BIN_TO_UUID(nu.user_id), ':', nu.notification_id)
         JOIN (SELECT 1 AS ace_order, @write AS mask
               UNION ALL
               SELECT 2, @delete) AS perms;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT
    aoi.id,
    1,
    sid.id,
    @write,
    1,
    0,
    0
FROM topic_subscription ts
         JOIN user u ON u.id = ts.user_id
         JOIN acl_sid sid ON sid.principal = 1 AND sid.sid = CONCAT('LOCAL:', u.username)
         JOIN acl_object_identity aoi ON aoi.object_id_identity = CONCAT(BIN_TO_UUID(ts.user_id), ':', ts.notification_topic_id)
         LEFT JOIN acl_entry ae ON ae.acl_object_identity = aoi.id AND ae.ace_order = 1
WHERE ae.id IS NULL;

INSERT INTO acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (@authentication_obj_id, 2, @role_anonymous, @read, 1, 1, 1),
       (@authentication_obj_id, 3, @role_anonymous, @create, 1, 1, 1),
       (@authentication_obj_id, 4, @role_anonymous, @write, 1, 1, 1),
       (@authentication_obj_id, 5, @role_anonymous, @delete, 1, 1, 1),
       (@authentication_obj_id, 6, @role_authen_id, @delete, 1, 1, 1),
       (@topic_sub_obj_id, 2, @role_authen_id, @view, 1, 1, 1),
       (@noti_user_obj_id, 2, @role_authen_id, @view, 1, 1, 1);

INSERT INTO acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT aoi.id, seq.ace_order, aoi.owner_sid, seq.mask, 1, 1, 1
FROM acl_object_identity aoi
         JOIN (SELECT 1 AS ace_order, @read AS mask
               UNION ALL
               SELECT 2 AS ace_order, @write AS mask) seq ON 1 = 1
WHERE aoi.object_id_class = @acl_class_user_id
  AND aoi.id <> @user_obj_id;