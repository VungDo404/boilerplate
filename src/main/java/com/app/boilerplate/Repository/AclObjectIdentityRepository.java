package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.AclObjectIdentity;
import com.app.boilerplate.Shared.Authorization.Model.AclObjectIdentityWithLevelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AclObjectIdentityRepository extends JpaRepository<AclObjectIdentity, Long> {
	@Query(value = """
        WITH RECURSIVE descendants AS (
            SELECT
                id,
                object_id_class,
                object_id_identity,
                parent_object,
                0 AS level
            FROM acl_object_identity
            WHERE
                (:rootId IS NULL AND parent_object IS NULL)
                OR id = :rootId

            UNION ALL

            SELECT
                aoi.id,
                aoi.object_id_class,
                aoi.object_id_identity,
                aoi.parent_object,
                d.level + 1 AS level
            FROM acl_object_identity aoi
            INNER JOIN descendants d ON aoi.parent_object = d.id
            WHERE d.level < 5
        )
        SELECT id, object_id_class, object_id_identity, parent_object, level FROM descendants
        """, nativeQuery = true)
	List<AclObjectIdentityWithLevelModel> findAllDescendantsWithLevel(@Param("rootId") Long rootId);
}
