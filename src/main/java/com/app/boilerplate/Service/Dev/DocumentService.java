package com.app.boilerplate.Service.Dev;

import com.app.boilerplate.Domain.Dev.Document;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Mapper.IDevMapper;
import com.app.boilerplate.Repository.DocumentRepository;
import com.app.boilerplate.Shared.Dev.Document.Dto.CreateDocumentDto;
import com.app.boilerplate.Shared.Dev.Document.Dto.PutDocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile("!prod")
@RequiredArgsConstructor
@Transactional
@Service
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final IDevMapper devMapper;

	@Transactional(readOnly = true)
	public Page<Document> findAll(Pageable pageable) {
		return documentRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Document getDocumentById(Integer id) {
		return Optional.of(id)
			.flatMap(documentRepository::findOneById)
			.orElseThrow(() -> new NotFoundException("Document not found", ""));
	}

	public Document createDocument(CreateDocumentDto document) {
		return Optional.of(document)
			.map(devMapper::toDocument)
			.map(documentRepository::save)
			.orElseThrow();
	}

	public Document updateDocument(PutDocumentDto document) {
		return Optional.of(document.getId())
			.map(this::getDocumentById)
			.map(d -> {
				devMapper.updateDocument(d, document);
				return documentRepository.save(d);
			})
			.orElseThrow();
	}

	public void deleteDocument(Integer id) {
		documentRepository.deleteById(id);
	}
}
