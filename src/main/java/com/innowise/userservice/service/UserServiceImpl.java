package com.innowise.userservice.service;

import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.interfaces.UserRepository;
import com.innowise.userservice.service.DTO.UserRequestDTO;
import com.innowise.userservice.service.DTO.UserResponseDTO;
import com.innowise.userservice.service.interfaces.UserService;
import com.innowise.userservice.service.interfaces.mappers.UserMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.innowise.userservice.service.utils.ServiceConstants.NO_USER_ERROR_MESSAGE;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService<UserResponseDTO,
        UserRequestDTO, Long> {

    private final UserServiceImpl self;
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(@Lazy UserServiceImpl self, UserRepository repository, UserMapper mapper) {
        this.self = self;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(value = "user_list")
    public List<UserResponseDTO> readAll() {
        return mapper.userListToDTOList(repository.findAll());
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserResponseDTO readById(Long id) {
        return mapper.userToUserDto(repository.findById(id).orElseThrow(() ->
                new NoSuchElementException(NO_USER_ERROR_MESSAGE + id)));
    }

    @Override
    @Transactional
    @CacheEvict(value = "user_list", allEntries = true)
    public UserResponseDTO create(UserRequestDTO createRequest) {
        User user = mapper.userDtoToUser(createRequest);
        return mapper.userToUserDto(repository.saveAndFlush(user));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public UserResponseDTO update(Long id, UserRequestDTO updateRequest) {
        if (!Objects.equals(id, updateRequest.id())) {
            throw new IllegalArgumentException(
                    String.format("Path ID (%d) and Request ID (%d) must match",
                            id, updateRequest.id()));
        }
        User targetUser = repository.findById(updateRequest.id()).orElseThrow(() ->
                new NoSuchElementException(NO_USER_ERROR_MESSAGE + updateRequest.id()));

        mapper.updateFromDto(updateRequest, targetUser);

        return mapper.userToUserDto(repository.saveAndFlush(targetUser));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public void deleteById(Long id) {
        self.deactivateUser(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public UserResponseDTO activateUser(Long id) {
        return mapper.userToUserDto(repository.activateUser(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public UserResponseDTO deactivateUser(Long id) {
        return mapper.userToUserDto(repository.deactivateUser(id));
    }

    @Override
    public Page<UserResponseDTO> readAll(UserRequestDTO request,
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
