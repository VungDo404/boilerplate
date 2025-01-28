package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.AclEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AclEntryRepository extends JpaRepository<AclEntry, Long> {
	Page<AclEntry> findBySid(Pageable pageable, Long sid);
	@Query(value = """
            SELECT ae
            FROM AclEntry ae
            WHERE ae.sid.id IN (
                SELECT s.id
                FROM AclSid s
                WHERE s.sid = (
                    SELECT CONCAT(u.provider, ':', u.username)
                    FROM User u
                    WHERE u.id = :userId
                )
                UNION
                SELECT a.sid.id
                FROM Authority a
                WHERE a.user.id = :userId
            )
            """)
	Page<AclEntry> findByUserId(Pageable pageable,@Param("userId") UUID userId);
}
