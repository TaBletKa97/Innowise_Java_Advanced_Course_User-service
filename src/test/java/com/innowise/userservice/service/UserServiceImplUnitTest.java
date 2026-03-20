package com.innowise.userservice.service;

import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.interfaces.UserRepository;
import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.service.dto.UserResponseDto;
import com.innowise.userservice.service.exceptions.ActivationException;
import com.innowise.userservice.service.exceptions.DeactivationException;
import com.innowise.userservice.service.interfaces.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.innowise.userservice.utils.TestsConstants.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @Mock
    private UserServiceImpl self;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;
    private UserRequestDto userRequestDto;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setActive(true);

        userResponseDto = new UserResponseDto(1L, NAME_ARYA, STARK, null, EMAIL_ARYA, true, null, null, List.of());
        userRequestDto = new UserRequestDto(1L, NAME_ARYA, STARK, null, EMAIL_ARYA, true);
    }

    @Test
    void readAllTest() {
        List<User> userList = List.of(user);
        when(repository.findAll()).thenReturn(userList);
        when(mapper.userListToDTOList(userList)).thenReturn(List.of(userResponseDto));

        List<UserResponseDto> responseDtos = userService.readAll();

        assertNotNull(responseDtos);
        verify(repository).findAll();
    }



    @Test
    void readByIdTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.userToUserDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.readById(1L);

        assertThat(result).isNotNull();
        verify(repository).findById(1L);
    }

    @Test
    void readByIdTrowsNoSuchElementException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.readById(1L));
    }

    @Test
    void createTest() {
        when(mapper.userDtoToUser(userRequestDto)).thenReturn(user);
        when(repository.saveAndFlush(user)).thenReturn(user);
        when(mapper.userToUserDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.create(userRequestDto);

        assertThat(result).isEqualTo(userResponseDto);
        verify(repository).saveAndFlush(user);
    }

    @Test
    void updateTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.saveAndFlush(user)).thenReturn(user);
        when(mapper.userToUserDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.update(1L, userRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.surname()).isEqualTo(STARK);

        verify(mapper).updateFromDto(userRequestDto, user);
        verify(repository).saveAndFlush(user);
    }

    @Test
    void updateThrowsIllegalArgumentException() {
        Long pathId = 1L;
        UserRequestDto requestWithOtherId = new UserRequestDto(99L, NAME_ARYA, STARK, null, EMAIL_ARYA, true);

        assertThrows(IllegalArgumentException.class, () -> userService.update(pathId, requestWithOtherId));

        verifyNoInteractions(repository);
    }

    @Test
    void deleteByIdTest() {
        userService.deleteById(1L);

        verify(self).deactivateUser(1L);
    }


    @Test
    void activateUserTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.saveAndFlush(user)).thenReturn(user);
        when(mapper.userToUserDto(user)).thenReturn(userResponseDto);
        user.setActive(false);

        userService.activateUser(1L);

        assertThat(user.isActive()).isTrue();
        verify(repository).saveAndFlush(user);
    }

    @Test
    void activateUserThrowsActivationException() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ActivationException.class, () -> userService.activateUser(1L));
    }

    @Test
    void deactivateUserTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.saveAndFlush(user)).thenReturn(user);
        when(mapper.userToUserDto(user)).thenReturn(userResponseDto);

        userService.deactivateUser(1L);

        assertThat(user.isActive()).isFalse();
        verify(repository).saveAndFlush(user);
    }

    @Test
    void deactivateUserThrowsDeactivationException() {
        user.setActive(false);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(DeactivationException.class, () -> userService.deactivateUser(1L));
    }

    @Test
    void readAllWithParamTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage = new PageImpl<>(List.of(user));

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(mapper.userToUserDto(user)).thenReturn(userResponseDto);

        Page<UserResponseDto> result = userService.readAll(userRequestDto, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(userResponseDto);

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).userToUserDto(any(User.class));
    }

}