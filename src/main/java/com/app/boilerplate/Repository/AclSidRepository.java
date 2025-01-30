package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.AclSid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AclSidRepository extends JpaRepository<AclSid, Long> {
	List<AclSid> findByPrincipal(boolean principal);
}
