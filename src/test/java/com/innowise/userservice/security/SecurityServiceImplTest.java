package com.innowise.userservice.security;

import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.service.interfaces.CardService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private JwtTokenUtils tokenUtils;

    @Mock
    private CardService<CardResponseDto, CardRequestDto, Long> cardService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    void canCreateUserNormalFlowTest() {

        String email = "user@test.com";
        Long userId = 100L;
        UserRequestDto request = new UserRequestDto(
                userId, "null", "null", null, email, true
        );

        Claims claims = mock(Claims.class);
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getDetails()).thenReturn(claims);
        when(tokenUtils.getLoginFromClaims(claims)).thenReturn(email);
        when(tokenUtils.getIdFromClaims(claims)).thenReturn(userId);


        boolean result = securityService.canCreateUser(request);

        assertTrue(result);
    }

    @Test
    void canCreateUserThrowsIllegalArgumentException() {

        String email = "user@test.com";
        String tokenEmail = "vasya@test.com";
        Long userId = 100L;
        UserRequestDto request = new UserRequestDto(
                userId, "null", "null", null, email, true
        );

        Claims claims = mock(Claims.class);
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getDetails()).thenReturn(claims);
        when(tokenUtils.getLoginFromClaims(claims)).thenReturn(tokenEmail);

        assertThrows(IllegalArgumentException.class, () ->
                securityService.canCreateUser(request));
    }


    @Test
    void canAccessUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getDetails()).thenReturn(null);
        when(tokenUtils.getIdFromClaims(any())).thenReturn(100L);

        assertTrue(securityService.canAccessUser(100L));
    }

    @Test
    void canAccessCard() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CardResponseDto cardResponseDto =
                new CardResponseDto(1L, 100L, null, null, null, true, null, null);

        when(authentication.getDetails()).thenReturn(null);
        when(tokenUtils.getIdFromClaims(any())).thenReturn(100L);
        when(cardService.readById(1L)).thenReturn(cardResponseDto);

        assertTrue(securityService.canAccessCard(1L));
    }
}
