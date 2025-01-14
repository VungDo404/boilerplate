SET @role_admin_id = (SELECT id FROM acl_sid WHERE sid = 'ROLE_APPLICATION_ADMINISTRATOR' AND principal = 0);
SET @role_authenticate_id = (SELECT id FROM acl_sid WHERE sid = 'ROLE_AUTHENTICATE' AND principal = 0);
SET @admin_id = (SELECT id FROM user WHERE username = 'admin' AND provider = 0);
SET @user_id = (SELECT id FROM user WHERE username = 'user' AND provider = 0);
SET @bob_id = (SELECT id FROM user WHERE username = 'bob@gmail.com' AND provider = 0);
SET @john_id = (SELECT id FROM user WHERE username = 'john@gmail.com' AND provider = 0);

INSERT INTO authority(user_id, sid_id)
VALUES (@admin_id, @role_admin_id),
       (@user_id, @role_authenticate_id),
       (@bob_id, @role_authenticate_id),
       (@john_id, @role_authenticate_id);
