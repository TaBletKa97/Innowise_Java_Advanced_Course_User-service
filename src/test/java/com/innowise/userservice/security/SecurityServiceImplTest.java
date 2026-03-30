package com.innowise.userservice.security;

import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.service.interfaces.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private CardService<CardResponseDto, CardRequestDto, Long> cardService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    void canAccessUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(100L);

        assertTrue(securityService.canAccessUser(100L));
    }

    @Test
    void canAccessCard() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CardResponseDto cardResponseDto =
                new CardResponseDto(1L, 100L, null, null, null, true, null, null);

        when(authentication.getPrincipal()).thenReturn(100L);
        when(cardService.readById(1L)).thenReturn(cardResponseDto);

        assertTrue(securityService.canAccessCard(1L));
    }
}
