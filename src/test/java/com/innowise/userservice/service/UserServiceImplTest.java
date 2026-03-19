package com.innowise.userservice.service;

import com.innowise.userservice.repository.exceptions.DeactivationException;
import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.service.dto.UserResponseDto;
import com.innowise.userservice.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static com.innowise.userservice.utils.TestsConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceImplTest extends BaseTest {

    @Autowired
    private UserServiceImpl userService;



    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void readAll() {
        List<UserResponseDto> userResponseDtos = userService.readAll();
        userResponseDtos.forEach(System.out::println);
        assertThat(userResponseDtos).hasSize(10);
    }

    @Test
    void readById() {
        UserResponseDto user = userService.readById(5L);
        assertEquals(5, user.id());
    }

    @Test
    void create() {
        UserResponseDto result = userService.create(new UserRequestDto(null,
                NAME_JON, SNOW, LocalDate.now(),
                EMAIL_JON, true));
        assertEquals(11, result.id());
        assertEquals(NAME_JON, result.name());
        assertEquals(SNOW, result.surname());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
    }

    @Test
    void update() {
        UserResponseDto result = userService.update(5L, new UserRequestDto(5L,
                NAME_JON, null, null,
                EMAIL_JON, true));
        assertEquals(5, result.id());
        assertEquals(NAME_JON, result.name());
        assertEquals(EMAIL_JON, result.email());
        System.out.println(result);
        assertNotEquals(result.createdAt(), result.updatedAt());
    }

    @Test
    void deleteById() {
        userService.deleteById(1L);
        assertThrows(DeactivationException.class, () -> userService.deleteById(1L));
    }

    @Test
    void activateUser() {
        UserResponseDto userResponseDTO = userService.activateUser(9L);
        assertTrue(userResponseDTO.active());
    }

    @Test
    void deactivateUser() {
        assertFalse(userService.deactivateUser(1L).active());
    }

    @Test
    void testReadAll() {
        UserRequestDto jonSnowWithoutEmailRequest = new UserRequestDto(null, NAME_JON, SNOW,
                LocalDate.now(), null, true);
        UserRequestDto jonSnow1Request = new UserRequestDto(null, NAME_JON, SNOW,
                LocalDate.now(), EMAIL_JON, true);
        UserRequestDto jonSnow2Request = new UserRequestDto(null, NAME_JON, SNOW,
                LocalDate.now(), 1 + EMAIL_JON, true);
        UserRequestDto jonSnow3Request = new UserRequestDto(null, NAME_JON, SNOW,
                LocalDate.now(), 2 + EMAIL_JON, true);
        UserRequestDto starkRequest = new UserRequestDto(null, NAME_NED, STARK,
                LocalDate.now(), 3 + EMAIL_JON, true);

        userService.create(jonSnow1Request);
        userService.create(jonSnow2Request);
        userService.create(jonSnow3Request);
        userService.create(starkRequest);

        Page<UserResponseDto> jonSnowPages = userService.readAll(jonSnowWithoutEmailRequest, PageRequest.of(0, 2));
        Page<UserResponseDto> starkPages = userService.readAll(starkRequest, PageRequest.of(0, 2));

        assertEquals(3, jonSnowPages.getTotalElements());
        assertEquals(2, jonSnowPages.getTotalPages());

        assertEquals(1, starkPages.getTotalElements());
        assertEquals(1, starkPages.getTotalPages());

    }
}