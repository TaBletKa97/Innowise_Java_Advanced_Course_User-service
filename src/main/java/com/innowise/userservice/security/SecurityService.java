package com.innowise.userservice.security;

import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.UserRequestDto;

public interface SecurityService {

    boolean canAccessUser(Long userId);
    boolean canAccessCard(Long cardId);
    boolean canCreateUser(UserRequestDto createRequest);
    boolean canCreateCard(CardRequestDto createRequest);

}
