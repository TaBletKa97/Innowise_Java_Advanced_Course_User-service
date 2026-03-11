package com.innowise.userservice.controller;

import com.innowise.userservice.service.DTO.CardRequestDTO;
import com.innowise.userservice.service.DTO.CardResponseDTO;
import com.innowise.userservice.service.DTO.UserRequestDTO;
import com.innowise.userservice.service.DTO.UserResponseDTO;
import com.innowise.userservice.service.interfaces.CardService;
import com.innowise.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService<UserResponseDTO, UserRequestDTO, Long> userService;
    private final CardService<CardResponseDTO, CardRequestDTO, Long> cardService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.readById(id));
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody @Validated UserRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestBody @Validated UserRequestDTO request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PostMapping("/search")
    public ResponseEntity<?> findUsersByCriteria(@RequestBody UserRequestDTO request,
                                                 Pageable pageable) {
        return ResponseEntity.ok().body(userService.readAll(request,pageable));
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<?> getAllCardsByUserId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.readAllCardsByUserId(id));
    }
}
