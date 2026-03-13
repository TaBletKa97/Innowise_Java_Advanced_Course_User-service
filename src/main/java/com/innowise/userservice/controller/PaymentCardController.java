package com.innowise.userservice.controller;

import org.springframework.web.bind.annotation.*;

import com.innowise.userservice.service.CardServiceImpl;
import com.innowise.userservice.service.DTO.CardRequestDTO;
import com.innowise.userservice.service.DTO.CardResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class PaymentCardController {

    private final CardServiceImpl cardService;

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> getAllCards() {
        return ResponseEntity.ok(cardService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.readById(id));
    }

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(
            @RequestBody CardRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDTO> updateCard(
            @RequestBody CardRequestDTO request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cardService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCard(@PathVariable("id") Long id) {
        cardService.deleteById(id);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CardResponseDTO> activateCard(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CardResponseDTO> deactivateCard(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.deactivateCard(id));
    }
}