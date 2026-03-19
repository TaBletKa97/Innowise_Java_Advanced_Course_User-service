package com.innowise.userservice.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserRequestDto(
        @Positive
        Long id,

        @Size(min = 2, max = 50)
        String name,

        @Size(min = 2, max = 50)
        String surname,

        LocalDate birthDate,

        @Email
        String email,

        Boolean active) {
}
