package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Authorization.AclClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}
