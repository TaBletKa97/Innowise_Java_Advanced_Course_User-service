package com.innowise.userservice.controller.interfaces.mappers;

import com.innowise.userservice.controller.DTO.UserRequestDTO;
import com.innowise.userservice.controller.DTO.UserResponseDTO;
import com.innowise.userservice.model.entity.User;
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
