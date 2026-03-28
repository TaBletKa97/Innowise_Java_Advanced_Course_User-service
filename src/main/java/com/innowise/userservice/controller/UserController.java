package com.innowise.userservice.controller;

import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.service.dto.UserResponseDto;
import com.innowise.userservice.service.interfaces.CardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.innowise.userservice.service.dto.CardResponseDto;
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
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService<UserResponseDto, UserRequestDto, Long> userService;
    private final CardService<CardResponseDto, CardRequestDto, Long> cardService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.readAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @ssi.canAccessUser(#id)")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.readById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Validated UserRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @ssi.canAccessUser(#id)")
    public ResponseEntity<UserResponseDto> updateUser(
            @RequestBody @Validated UserRequestDto request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDto> activateUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN') or @ssi.canAccessUser(#id)")
    public ResponseEntity<UserResponseDto> deactivateUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDto>> findUsersByCriteria(UserRequestDto request,
                                                                     Pageable pageable) {
        return ResponseEntity.ok().body(userService.readAll(request,pageable));
    }

    @GetMapping("/{id}/cards")
    @PreAuthorize("hasAuthority('ADMIN') or @ssi.canAccessUser(#id)")
    public ResponseEntity<List<CardResponseDto>> getAllCardsByUserId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cardService.readAllCardsByUserId(id));
    }
}
