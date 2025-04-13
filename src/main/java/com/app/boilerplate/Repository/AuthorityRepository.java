package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.Authority;
import com.app.boilerplate.Domain.Authorization.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {
    @Query(value = """
        SELECT s.sid
        FROM authority a
        JOIN acl_sid s
        ON a.sid_id = s.id
        WHERE a.user_id = UUID_TO_BIN(:userId)
        ORDER BY a.priority DESC
        """, nativeQuery = true)
    List<String> findSidsByUserId(String userId);

    @Query(value = """
        SELECT ae.mask, ac.`class`, ae.granting, aoi.object_id_identity
        FROM acl_entry ae
        JOIN acl_object_identity aoi ON aoi.id = ae.acl_object_identity
        JOIN acl_class ac ON ac.id = aoi.object_id_class
        JOIN (
            SELECT id, 0 AS sort_order, NULL AS priority
            FROM acl_sid
            WHERE principal = 1 AND sid = :sidValue
            UNION ALL
            SELECT s.id, 1 AS sort_order, a.priority
            FROM authority a
            JOIN acl_sid s ON a.sid_id = s.id
            WHERE a.user_id = UUID_TO_BIN(:userId)
        ) ordered_sids ON ae.sid = ordered_sids.id
        ORDER BY ordered_sids.sort_order, ordered_sids.priority DESC
        """, nativeQuery = true)
    List<Object[]> findAclEntriesByUserId(@Param("userId") String userId, @Param("sidValue") String sidValue);

    @Query(value = """
    SELECT ae.mask, ac.`class`, ae.granting, aoi.object_id_identity
    FROM acl_entry ae
    JOIN acl_object_identity aoi
    ON aoi.id = ae.acl_object_identity
    JOIN acl_class ac
    ON ac.id = aoi.object_id_class
    JOIN (
        SELECT id
        FROM acl_sid
        WHERE principal = 0 AND sid IN :authorities
    ) ordered_sids ON ae.sid = ordered_sids.id
    """, nativeQuery = true)
    List<Object[]> findAclEntriesBySid(@Param("authorities") Collection<String> authorities);


}
