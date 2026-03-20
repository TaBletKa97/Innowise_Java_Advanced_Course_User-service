package com.innowise.userservice.controller;

import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.utils.BaseTest;
import com.innowise.userservice.utils.IT;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IT
@AutoConfigureMockMvc
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alexey"));
    }

    @Test
    void createUser() throws Exception {
        UserRequestDto request = new UserRequestDto(null, "SomeName",
                "SomeSurname", LocalDate.now(), "test@test.com", true);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void updateUser() throws Exception {
        UserRequestDto request = new UserRequestDto(1L, null,
                "SomeSurname", null, null, null);
        MvcResult result = mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.surname").value("SomeSurname"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        LocalDateTime createdAt = LocalDateTime.parse(JsonPath.read(json, "$.createdAt"));
        LocalDateTime updatedAt = LocalDateTime.parse(JsonPath.read(json, "$.updatedAt"));
        assertTrue(updatedAt.isAfter(createdAt));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateUser() throws Exception {
        mockMvc.perform(patch("/users/3/activate"))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateUser() throws Exception {
        mockMvc.perform(patch("/users/1/deactivate"))
                .andExpect(status().isOk());
    }

    @Test
    void findUsersByCriteriaTest() throws Exception {
        UserRequestDto request = new UserRequestDto(null, null,
                "Sidorov", null, null, null);

        mockMvc.perform(get("/users/search")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].surname").value("Sidorov"))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void findUsersByEmailTest() throws Exception {
        final String EXPECTED_EMAIL = "elena@example.com";
        UserRequestDto request = new UserRequestDto(null, null,
                null, null, EXPECTED_EMAIL, null);

        mockMvc.perform(get("/users/search")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value(EXPECTED_EMAIL))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(3));
    }

    @Test
    void getAllCardsByUserId() throws Exception {
        mockMvc.perform(get("/users/4/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}