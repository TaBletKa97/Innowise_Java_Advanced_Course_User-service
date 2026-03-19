package com.innowise.userservice.service.interfaces.mappers;

import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.service.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE, uses = CardMapper.class)
public interface UserMapper {

    List<UserResponseDto> userListToDTOList(List<User> userList);

    UserResponseDto userToUserDto(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User userDtoToUser(UserRequestDto user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    void updateFromDto(UserRequestDto request, @MappingTarget User target);
}
