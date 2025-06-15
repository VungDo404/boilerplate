package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Security.OAuth2UserInfo;
import com.app.boilerplate.Shared.User.Dto.CreateUserDto;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.UpdateUserDto;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface IUserMapper {
    User toUser(PostUserDto dto);
    User toUser(CreateUserDto dto);
    UpdateUserDto toUpdateUserDto(User user);

    @Mapping(source = "id", target = "username")
    @Mapping(source = "imageUrl", target = "image")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "displayName")
    @Mapping(target = "emailSpecify", expression = "java(java.time.LocalDateTime.now())")
    PostUserDto toPostUserDto(OAuth2UserInfo info);

    void update(@MappingTarget User user, UpdateUserDto updateUserDto);

    void update(@MappingTarget User user, String password);

}
