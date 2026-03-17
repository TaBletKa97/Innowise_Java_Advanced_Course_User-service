package com.innowise.userservice.service;

import com.innowise.userservice.repository.exceptions.DeactivationException;
import com.innowise.userservice.service.DTO.UserRequestDTO;
import com.innowise.userservice.service.DTO.UserResponseDTO;
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
        List<UserResponseDTO> userResponseDTOS = userService.readAll();
        userResponseDTOS.forEach(System.out::println);
        assertThat(userResponseDTOS).hasSize(10);
    }

    @Test
    void readById() {
        UserResponseDTO user = userService.readById(5L);
        assertEquals(5, user.id());
    }

    @Test
    void create() {
        UserResponseDTO result = userService.create(new UserRequestDTO(null,
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
        UserResponseDTO result = userService.update(5L, new UserRequestDTO(5L,
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
        UserResponseDTO userResponseDTO = userService.activateUser(9L);
        assertTrue(userResponseDTO.active());
    }

    @Test
    void deactivateUser() {
        assertFalse(userService.deactivateUser(1L).active());
    }

    @Test
    void testReadAll() {
        UserRequestDTO jonSnowWithoutEmailRequest = new UserRequestDTO(null, NAME_JON, SNOW,
                LocalDate.now(), null, true);
        UserRequestDTO jonSnow1Request = new UserRequestDTO(null, NAME_JON, SNOW,
                LocalDate.now(), EMAIL_JON, true);
        UserRequestDTO jonSnow2Request = new UserRequestDTO(null, NAME_JON, SNOW,
                LocalDate.now(), 1 + EMAIL_JON, true);
        UserRequestDTO jonSnow3Request = new UserRequestDTO(null, NAME_JON, SNOW,
                LocalDate.now(), 2 + EMAIL_JON, true);
        UserRequestDTO starkRequest = new UserRequestDTO(null, NAME_NED, STARK,
                LocalDate.now(), 3 + EMAIL_JON, true);

        userService.create(jonSnow1Request);
        userService.create(jonSnow2Request);
        userService.create(jonSnow3Request);
        userService.create(starkRequest);

        Page<UserResponseDTO> jonSnowPages = userService.readAll(jonSnowWithoutEmailRequest, PageRequest.of(0, 2));
        Page<UserResponseDTO> starkPages = userService.readAll(starkRequest, PageRequest.of(0, 2));

        assertEquals(3, jonSnowPages.getTotalElements());
        assertEquals(2, jonSnowPages.getTotalPages());

        assertEquals(1, starkPages.getTotalElements());
        assertEquals(1, starkPages.getTotalPages());

    }
}