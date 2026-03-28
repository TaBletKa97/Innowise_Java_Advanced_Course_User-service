package com.innowise.userservice.security;

import com.innowise.userservice.service.dto.CardRequestDto;

public interface SecurityService {

    boolean canAccessUser(Long userId);
    boolean canAccessCard(Long cardId);
    boolean canCreateCard(CardRequestDto createRequest);

}
