package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.Authorization.Authority;
import com.app.boilerplate.Shared.Authorization.Dto.CreateAuthorityDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface IAuthorizeMapper {
	Authority toAuthority(CreateAuthorityDto authorityDto);
}
