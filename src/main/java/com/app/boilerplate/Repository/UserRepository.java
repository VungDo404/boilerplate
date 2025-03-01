package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, RevisionRepository<User, UUID, Integer> {
    Optional<User> findOneByEmailIgnoreCase(String email);

	Optional<User> findOneByUsernameAndProvider(String username, LoginProvider provider);

	boolean existsByEmailIgnoreCaseAndProvider(String email, LoginProvider provider);

    Page<User> findAll(@NonNull Specification<User> specification, @NonNull Pageable page);

}
