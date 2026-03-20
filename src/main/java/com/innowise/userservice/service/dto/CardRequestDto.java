package com.innowise.userservice.service.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CardRequestDto(
        @Positive
        Long id,

        @Positive
        @NotNull
        Long userId,

        @Pattern(regexp = "\\d{15,16}")
        String number,

        @Size(max = 50)
        String holder,

        @FutureOrPresent
        LocalDate expirationDate,

        Boolean active) {
}
