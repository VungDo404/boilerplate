package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.Authority;
import com.app.boilerplate.Domain.Authorization.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {
	@Query(value = """
        SELECT s.sid
        FROM authority a
        JOIN acl_sid s
        ON a.sid_id = s.id
        WHERE a.user_id = UUID_TO_BIN(:userId)
        """, nativeQuery = true)
	List<String> findSidsByUserId(String userId);


}
