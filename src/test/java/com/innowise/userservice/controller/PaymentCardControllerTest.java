package com.innowise.userservice.controller;

import com.innowise.userservice.service.DTO.CardRequestDTO;
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
class PaymentCardControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }


    @Test
    void getAllCards() throws Exception {
        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void getCardById() throws Exception {
        mockMvc.perform(get("/cards/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("1111222233334444"));
    }

    @Test
    void createCard() throws Exception {
        CardRequestDTO request = new CardRequestDTO(null, 1L,
                "2569223233334444", "holder",
                LocalDate.now().plusYears(1), true);
        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void updateCard() throws Exception {
        CardRequestDTO request = new CardRequestDTO(1L, 1L,
                "2569223233334444", null, null, null);
        MvcResult result = mockMvc.perform(put("/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("2569223233334444"))
                .andExpect(jsonPath("$.holder").value("IVAN IVANOV"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        LocalDateTime createdAt = LocalDateTime.parse(JsonPath.read(json, "$.createdAt"));
        LocalDateTime updatedAt = LocalDateTime.parse(JsonPath.read(json, "$.updatedAt"));
        assertTrue(updatedAt.isAfter(createdAt));
    }

    @Test
    void deleteCard() throws Exception {
        mockMvc.perform(delete("/cards/1"))
                .andExpect(status().isOk());
    }

    @Test
    void activateCard() throws Exception {
        mockMvc.perform(patch("/cards/5/activate"))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateCard() throws Exception {
        mockMvc.perform(patch("/cards/1/deactivate"))
                .andExpect(status().isOk());
    }
}