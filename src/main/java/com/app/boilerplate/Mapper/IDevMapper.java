package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.Dev.Document;
import com.app.boilerplate.Domain.Dev.Organization;
import com.app.boilerplate.Shared.Dev.Document.Dto.CreateDocumentDto;
import com.app.boilerplate.Shared.Dev.Document.Dto.PutDocumentDto;
import com.app.boilerplate.Shared.Dev.Organization.Dto.CreateOrganizationDto;
import com.app.boilerplate.Shared.Dev.Organization.Dto.PutOrganizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IDevMapper {
	Document toDocument(CreateDocumentDto dto);
	void updateDocument(@MappingTarget Document document, PutDocumentDto dto);
	Organization toOrganization(CreateOrganizationDto dto);
	void updateOrganization(@MappingTarget Organization organization, PutOrganizationDto dto);
}
