package com.app.boilerplate.Controller.Dev;

import com.app.boilerplate.Decorator.RateLimit.RateLimit;
import com.app.boilerplate.Domain.Dev.Document;
import com.app.boilerplate.Service.Dev.DocumentService;
import com.app.boilerplate.Shared.Dev.Document.Dto.CreateDocumentDto;
import com.app.boilerplate.Shared.Dev.Document.Dto.PutDocumentDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.CREATED;

@RateLimit(capacity = 10, tokens = 1, duration = 10, timeUnit = ChronoUnit.SECONDS, key = "'document-' + #ip")
@Profile("!prod")
@Tag(name = "Document")
@RequiredArgsConstructor
@RequestMapping("/document")
@RestController
public class DocumentController {
	private final DocumentService documentService;
	@PreAuthorize("hasPermission(0, 'com.app.boilerplate.Domain.Dev.Document', 'READ')")
	@GetMapping("/")
	public Page<Document> getAll(@ParameterObject Pageable pageable) {
		return documentService.findAll(pageable);
	}

	@PreAuthorize("hasPermission(#id, 'com.app.boilerplate.Domain.Dev.Document', 'READ')")
	@GetMapping("/{id}")
	public Document getById(@PathVariable @NotNull Integer id) {
		return documentService.getDocumentById(id);
	}

	@PreAuthorize("hasPermission(0, 'com.app.boilerplate.Domain.Dev.Document', 'CREATE')")
	@ResponseStatus(CREATED)
	@PostMapping()
	public Document create(@RequestBody @Valid CreateDocumentDto DocumentDto) {
		return documentService.createDocument(DocumentDto);
	}

	@PreAuthorize("hasPermission(#request.id, 'com.app.boilerplate.Domain.Dev.Document', 'WRITE')")
	@ResponseStatus(HttpStatus.CREATED)
	@PutMapping
	public Document put(@RequestBody PutDocumentDto request) {
		return documentService.updateDocument(request);
	}

	@PreAuthorize("hasPermission(#id, 'com.app.boilerplate.Domain.Dev.Document', 'DELETE')")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable @NotNull Integer id) {
		documentService.deleteDocument(id);
	}
}

