package com.innowise.userservice.controller;

import com.innowise.userservice.controller.DTO.UserRequestDTO;
import com.innowise.userservice.controller.DTO.UserResponseDTO;
import com.innowise.userservice.controller.interfaces.UserService;
import com.innowise.userservice.controller.interfaces.mappers.UserMapper;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.interfaces.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;

import static com.innowise.userservice.controller.utils.ServiceConstants.NO_USER_ERROR_MESSAGE;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService<UserResponseDTO,
        UserRequestDTO, Long> {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserResponseDTO> readAll() {
        return mapper.userListToDTOList(repository.findAll());
    }

    @Override
    public UserResponseDTO readById(Long id) {
        return mapper.userToUserDto(repository.findById(id).orElseThrow(() ->
                new NoSuchElementException(NO_USER_ERROR_MESSAGE + id)));
    }

    @Override
    public UserResponseDTO create(@Validated UserRequestDTO createRequest) {
        User user = mapper.userDtoToUser(createRequest);
        return mapper.userToUserDto(repository.saveAndFlush(user));
    }

    @Override
    @Transactional
    public UserResponseDTO update(@Validated UserRequestDTO createRequest) {
        User targetUser = repository.findById(createRequest.id()).orElseThrow(() ->
                new NoSuchElementException(NO_USER_ERROR_MESSAGE + createRequest.id()));

        mapper.updateFromDto(createRequest, targetUser);

        return mapper.userToUserDto(repository.saveAndFlush(targetUser));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("There is no user with id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponseDTO activateUser(Long id) {
        return mapper.userToUserDto(repository.activateUser(id));
    }

    @Override
    @Transactional
    public UserResponseDTO deactivateUser(Long id) {
        return mapper.userToUserDto(repository.deactivateUser(id));
    }

    @Override
    public Page<UserResponseDTO> readAll(@Validated UserRequestDTO request,
                                         Pageable pageable) {
        Specification<User> spec = Specification.where(
                (r, q, cb) -> null);

        if (request.name() != null && !request.name().isEmpty()) {
            spec = spec.and(UserSpecification.containsName(request.name()));
        }

        if (request.surname() != null && !request.surname().isEmpty()) {
            spec = spec.and(UserSpecification.containsSurname(request.surname()));
        }

        return this.repository.findAll(spec, pageable).map(mapper::userToUserDto);
    }
}
