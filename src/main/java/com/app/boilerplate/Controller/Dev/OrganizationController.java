package com.app.boilerplate.Controller.Dev;

import com.app.boilerplate.Domain.Dev.Organization;
import com.app.boilerplate.Service.Dev.OrganizationService;
import com.app.boilerplate.Shared.Dev.Organization.Dto.CreateOrganizationDto;
import com.app.boilerplate.Shared.Dev.Organization.Dto.PutOrganizationDto;
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

import static org.springframework.http.HttpStatus.CREATED;

@Profile("!prod")
@Tag(name = "Organization")
@RequiredArgsConstructor
@RequestMapping("/organization")
@RestController
public class OrganizationController {
	private final OrganizationService organizationService;

	@PreAuthorize("hasPermission(#id, 'com.app.boilerplate.Domain.Dev.Organization', 'READ')")
	@GetMapping("/{id}")
	public Organization getById(@PathVariable @NotNull Integer id) {
		return organizationService.getOrganizationById(id);
	}

	@GetMapping("/")
	public Page<Organization> getAll(@ParameterObject Pageable pageable) {
		return organizationService.findAll(pageable);
	}


	@ResponseStatus(CREATED)
	@PostMapping()
	public Organization create(@RequestBody @Valid CreateOrganizationDto organizationDto) {
		return organizationService.createOrganization(organizationDto);
	}
	@PreAuthorize("hasPermission(#request.id, 'com.app.boilerplate.Domain.Dev.Organization', 'WRITE')")
	@ResponseStatus(HttpStatus.CREATED)
	@PutMapping
	public Organization put(@RequestBody PutOrganizationDto request) {
		return organizationService.updateOrganization(request);
	}

	@PreAuthorize("hasPermission(#id, 'com.app.boilerplate.Domain.Dev.Organization', 'DELETE')")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable @NotNull Integer id) {
		organizationService.deleteOrganization(id);
	}
}
