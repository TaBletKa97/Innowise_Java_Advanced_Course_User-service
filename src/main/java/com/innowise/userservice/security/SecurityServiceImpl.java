package com.innowise.userservice.security;

import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.service.dto.UserRequestDto;
import com.innowise.userservice.service.interfaces.CardService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ssi")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final JwtTokenUtils jwtTokenUtils;
    private final CardService<CardResponseDto, CardRequestDto, Long> cardService;

    @Override
    public boolean canAccessUser(Long userId) {
        Long currentUserId = getCurrentUserId();

        log.debug("User with id {} trying obtain information about user with id {}", currentUserId, userId);
        return userId.equals(currentUserId);
    }

    @Override
    public boolean canAccessCard(Long cardId) {
        Long currentUserId = getCurrentUserId();

        log.debug("User with id {} trying obtain information about card with id {}", currentUserId, cardId);
        return cardService.readById(cardId).userId().equals(currentUserId);

    }

    @Override
    public boolean canCreateUser(UserRequestDto createRequest) {
        Claims claims = (Claims) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        String login = jwtTokenUtils.getLoginFromClaims(claims);

        if (!login.equals(createRequest.email())) {
            throw new IllegalArgumentException("Please enter correct email");
        }

        return getCurrentUserId().equals(createRequest.id());
    }

    @Override
    public boolean canCreateCard(CardRequestDto createRequest) {
        return getCurrentUserId().equals(createRequest.userId());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return jwtTokenUtils.getIdFromClaims((Claims) authentication.getDetails());
    }
}
