package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Dev.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

@Profile("!prod")
public interface DocumentRepository extends JpaRepository<Document, Integer>, JpaSpecificationExecutor<Document>,
	RevisionRepository<Document, Integer, Integer> {
	Optional<Document> findOneById(Integer id);
}
