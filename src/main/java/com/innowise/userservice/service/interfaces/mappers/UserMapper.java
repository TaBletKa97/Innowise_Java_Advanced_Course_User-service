package com.innowise.userservice.service.interfaces.mappers;

import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.service.DTO.UserRequestDTO;
import com.innowise.userservice.service.DTO.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    List<UserResponseDTO> userListToDTOList(List<User> userList);

    UserResponseDTO userToUserDto(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User userDtoToUser(UserRequestDTO user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    void updateFromDto(UserRequestDTO request, @MappingTarget User target);
}
