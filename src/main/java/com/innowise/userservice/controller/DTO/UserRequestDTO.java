package com.innowise.userservice.controller.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserRequestDTO(
        @Positive
        Long id,

        @Size(min = 2, max = 50)
        String name,

        @Size(min = 2, max = 50)
        String surname,

        LocalDate birthDate,

        @Email
        String email,

        boolean active) {
}
