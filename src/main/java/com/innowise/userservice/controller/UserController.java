package com.innowise.userservice.controller;

import org.springframework.web.bind.annotation.*;

import com.innowise.userservice.service.DTO.CardResponseDTO;
import com.innowise.userservice.service.DTO.UserRequestDTO;
import com.innowise.userservice.service.DTO.UserResponseDTO;
import com.innowise.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService<UserResponseDTO, UserRequestDTO, Long> userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.readById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Validated UserRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @RequestBody @Validated UserRequestDTO request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> findUsersByCriteria(@RequestBody UserRequestDTO request,
                                                    Pageable pageable) {
        return ResponseEntity.ok().body(userService.readAll(request,pageable));
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardResponseDTO>> getAllCardsByUserId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.readById(id).cards());
    }
}
