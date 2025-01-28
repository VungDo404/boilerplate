package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.AclSid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AclSidRepository extends JpaRepository<AclSid, Long> {
}
