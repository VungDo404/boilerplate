package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.Account.Dto.RegisterDto;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.PutUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IUserMapper {
	User toUser(PostUserDto user);
	User toUser(RegisterDto user);

	void update(@MappingTarget User user, PutUserDto putUserDto);
	void update(@MappingTarget User user, String password);

}
