SET @admin_id = (SELECT id FROM acl_sid WHERE principal = 1 AND sid = 'LOCAL:admin');
SET @user_id = (SELECT id FROM acl_sid WHERE principal = 1 AND sid = 'LOCAL:user');
SET @bob_id = (SELECT id FROM acl_sid WHERE principal = 1 AND sid = 'LOCAL:bob@gmail.com');
SET @john_id = (SELECT id FROM acl_sid WHERE principal = 1 AND sid = 'LOCAL:john@gmail.com');
SET @role_admin_id = (SELECT id FROM acl_sid WHERE sid = 'ROLE_APPLICATION_ADMINISTRATOR' AND principal = 0);
SET @role_authenticate_id = (SELECT id FROM acl_sid WHERE sid = 'ROLE_AUTHENTICATE' AND principal = 0);

INSERT INTO document(description, title, is_approved, expiration)
VALUES ('Doc 1 Description', 'Doc 1 Title', 1, '2025-02-15 10:00:00'),
	   ('Doc 2 Description', 'Doc 2 Title', 0, NULL),
	   ('Doc 3 Description', 'Doc 3 Title', 1, '2025-03-01 15:30:00');

SELECT id INTO @doc1_id FROM document WHERE title = 'Doc 1 Title';
SELECT id INTO @doc2_id FROM document WHERE title = 'Doc 2 Title';
SELECT id INTO @doc3_id FROM document WHERE title = 'Doc 3 Title';

INSERT INTO organization(display_name)
VALUES ('Hummingbird Lighting'),
	   ('Nymph Microsystems'),
	   ('Alpha Security');

SELECT id INTO @org1_id FROM organization WHERE display_name = 'Hummingbird Lighting';
SELECT id INTO @org2_id FROM organization WHERE display_name = 'Nymph Microsystems';
SELECT id INTO @org3_id FROM organization WHERE display_name = 'Alpha Security';


INSERT INTO acl_class(class)
VALUES ('com.app.boilerplate.Domain.Dev.Document'),
	   ('com.app.boilerplate.Domain.Dev.Organization');

SELECT id INTO @acl_doc_id FROM acl_class WHERE class = 'com.app.boilerplate.Domain.Dev.Document';
SELECT id INTO @acl_org_id FROM acl_class WHERE class = 'com.app.boilerplate.Domain.Dev.Organization';

INSERT INTO acl_object_identity(object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
SELECT acl_class.id, 0, NULL, @role_admin_id, 1
FROM acl_class
WHERE NOT EXISTS(
	SELECT 1
	FROM acl_object_identity
	WHERE acl_class.id = acl_object_identity.object_id_class AND acl_object_identity.object_id_identity = 0
);

INSERT INTO acl_object_identity(object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES (@acl_doc_id,@doc1_id,NULL,@bob_id,1), -- john is able to read this
	   (@acl_doc_id,@doc2_id,NULL,@bob_id,1), -- john is able to delete this
	   (@acl_doc_id,@doc3_id,NULL,@john_id,1),
	   (@acl_org_id,@org1_id,NULL,@john_id,1), -- user, bob is able to read and update (write) this
	   (@acl_org_id,@org2_id,NULL,@john_id,1),
	   (@acl_org_id,@org3_id,NULL,@user_id,1);

UPDATE acl_object_identity child
JOIN acl_object_identity parent
ON child.object_id_class = parent.object_id_class AND parent.object_id_identity = 0
SET child.parent_object = parent.id
WHERE child.object_id_identity <> 0
	AND child.entries_inheriting = 1;

INSERT INTO acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT aoi.id, 1, @role_admin_id, 16, 1, 1, 1
FROM acl_object_identity aoi
WHERE aoi.object_id_identity = 0;

INSERT INTO acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT aoi.id,
       (SELECT COALESCE(MAX(ace_order),0) + 1 FROM acl_entry WHERE acl_object_identity = aoi.id),
       @role_authenticate_id,
       4, -- CREATE
       1,
       1,
       1
FROM acl_object_identity aoi
JOIN `boilerplate-master`.acl_class ac
	ON ac.id = aoi.object_id_class
WHERE aoi.object_id_identity = 0
  AND ac.class = 'com.app.boilerplate.Domain.Dev.Document';

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT
	aoi.id,
	(SELECT COALESCE(MAX(ace_order), 0) + 1 FROM acl_entry WHERE acl_object_identity = aoi.id),
	@john_id,
	1,  -- READ permission
	1, 1, 1
FROM acl_object_identity aoi
WHERE aoi.object_id_class = @acl_doc_id AND aoi.object_id_identity = @doc1_id;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT
	aoi.id,
	(SELECT COALESCE(MAX(ace_order), 0) + 1 FROM acl_entry WHERE acl_object_identity = aoi.id),
	@john_id,
	8,  -- DELETE permission
	1, 1, 1
FROM acl_object_identity aoi
WHERE aoi.object_id_class = @acl_doc_id AND aoi.object_id_identity = @doc2_id;

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT
	aoi.id,
	ROW_NUMBER() OVER () + COALESCE((SELECT MAX(ace_order) FROM acl_entry WHERE acl_object_identity = aoi.id), 0) AS ace_order,
	permissions.sid,
	permissions.mask,
	1, 1, 1
FROM acl_object_identity aoi
CROSS JOIN (
	SELECT @user_id AS sid, 1 AS mask -- user READ
	UNION ALL
	SELECT @user_id, 2               -- user WRITE
	UNION ALL
	SELECT @bob_id, 1                -- bob READ
	UNION ALL
	SELECT @bob_id, 2                -- bob WRITE
) permissions
WHERE aoi.object_id_class = @acl_org_id
  AND aoi.object_id_identity = @org1_id;
