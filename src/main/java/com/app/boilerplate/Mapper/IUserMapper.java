package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.PutUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IUserMapper {
	User toUser(PostUserDto dto);
	User toUser(CreateUserDto dto);

	void update(@MappingTarget User user, PutUserDto putUserDto);
	void update(@MappingTarget User user, String password);

}
