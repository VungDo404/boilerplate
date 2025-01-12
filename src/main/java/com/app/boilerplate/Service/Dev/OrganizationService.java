package com.app.boilerplate.Service.Dev;

import com.app.boilerplate.Domain.Dev.Organization;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Mapper.IDevMapper;
import com.app.boilerplate.Repository.OrganizationRepository;
import com.app.boilerplate.Shared.Dev.Organization.Dto.CreateOrganizationDto;
import com.app.boilerplate.Shared.Dev.Organization.Dto.PutOrganizationDto;
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
public class OrganizationService {
	private final OrganizationRepository organizationRepository;
	private final IDevMapper devMapper;

	@Transactional(readOnly = true)
	public Page<Organization> findAll(Pageable pageable) {
		return organizationRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Organization getOrganizationById(Integer id) {
		return Optional.of(id)
			.flatMap(organizationRepository::findOneById)
			.orElseThrow(() -> new NotFoundException("Organization not found", ""));
	}

	public Organization createOrganization(CreateOrganizationDto Organization) {
		return Optional.of(Organization)
			.map(devMapper::toOrganization)
			.map(organizationRepository::save)
			.orElseThrow();
	}

	public Organization updateOrganization(PutOrganizationDto Organization) {
		return Optional.of(Organization.getId())
			.map(this::getOrganizationById)
			.map(d -> {
				devMapper.updateOrganization(d, Organization);
				return organizationRepository.save(d);
			})
			.orElseThrow();
	}

	public void deleteOrganization(Integer id) {
		organizationRepository.deleteById(id);
	}
}

