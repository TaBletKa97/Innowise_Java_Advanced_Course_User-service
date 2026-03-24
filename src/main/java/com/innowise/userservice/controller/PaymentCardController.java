package com.innowise.userservice.controller;

import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.service.interfaces.CardService;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import com.innowise.userservice.service.dto.CardRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class PaymentCardController {

    private final CardService<CardResponseDto, CardRequestDto, Long> cardService;

    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
        return ResponseEntity.ok(cardService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.readById(id));
    }

    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(
            @RequestBody @Validated CardRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDto> updateCard(
            @RequestBody @Validated CardRequestDto request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cardService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("id") Long id) {
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CardResponseDto> activateCard(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CardResponseDto> deactivateCard(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.deactivateCard(id));
    }
}