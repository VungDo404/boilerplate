INSERT INTO notification_topic(name, subscribe_by_default)
VALUES ('general', true);

INSERT INTO notification(title, message, type, url)
VALUES ('notification.welcome.title', 'notification.welcome.message', 'INFO', null);

INSERT INTO topic_subscription(user_id, notification_topic_id, muted)
SELECT id, (SELECT id FROM notification_topic WHERE name = 'general'), false
FROM user;

INSERT INTO notification_user(notification_id, user_id, is_read)
SELECT (SELECT id FROM notification), id, false
FROM user;

INSERT INTO audit_envers_revision(timestamp, user_id)
VALUES (NOW(), UUID_TO_BIN(REPLACE('00000000-0000-0000-0000-000000000000', '-', '')));

INSERT INTO audit_envers_modified_entity(revision_id, entity_name)
VALUES (LAST_INSERT_ID(),'com.app.boilerplate.Domain.Notification.NotificationUser');

INSERT INTO notification_user_history (rev, rev_type, notification_id, user_id, is_read)
SELECT
    LAST_INSERT_ID(),
    0,
    nu.notification_id,
    nu.user_id,
    nu.is_read
FROM notification_user nu;