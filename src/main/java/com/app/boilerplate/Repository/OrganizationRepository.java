package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Dev.Organization;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

@Profile("!prod")
public interface OrganizationRepository extends JpaRepository<Organization, Integer>,
	JpaSpecificationExecutor<Organization>, RevisionRepository<Organization, Integer, Integer> {
	Optional<Organization> findOneById(Integer id);
}
