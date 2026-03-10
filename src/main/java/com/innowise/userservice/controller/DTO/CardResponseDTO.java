package com.innowise.userservice.controller.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CardResponseDTO(
        Long id,
        Long userId,
        String number,
        String holder,
        LocalDate expirationDate,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
