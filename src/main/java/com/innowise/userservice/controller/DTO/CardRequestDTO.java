package com.innowise.userservice.controller.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CardRequestDTO(
        @Positive
        Long id,

        @Positive
        Long userId,

        @Pattern(regexp = "\\d{15,16}")
        String number,

        String holder,

        LocalDate expirationDate,

        boolean active) {
}
