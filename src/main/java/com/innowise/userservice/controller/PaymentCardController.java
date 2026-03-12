package com.innowise.userservice.controller;

import com.innowise.userservice.service.CardServiceImpl;
import com.innowise.userservice.service.DTO.CardRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class PaymentCardController {

    private final CardServiceImpl cardService;

    @GetMapping
    public ResponseEntity<?> getAllCards() {
        return ResponseEntity.ok(cardService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.readById(id));
    }

    @PostMapping
    public ResponseEntity<?> createCard(
            @RequestBody CardRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(
            @RequestBody CardRequestDTO request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cardService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable("id") Long id) {
        cardService.deleteById(id);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateCard(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateCard(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.deactivateCard(id));
    }
}
