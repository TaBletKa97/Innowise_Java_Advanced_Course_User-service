package com.innowise.userservice.security;

import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.service.interfaces.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ssi")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

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
    public boolean canCreateCard(CardRequestDto createRequest) {
        return createRequest.userId().equals(getCurrentUserId());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) {
            return  null;
        }
        return (Long) authentication.getPrincipal();
    }
}
