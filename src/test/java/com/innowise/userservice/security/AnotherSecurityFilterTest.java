package com.innowise.userservice.security;

import com.innowise.userservice.exceptions.WrongHeaderException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnotherSecurityFilterTest {

    @Mock
    private FilterChain filterChain;

    private AnotherSecurityFilter anotherSecurityFilter;

    @BeforeEach
    void setUp() {
        anotherSecurityFilter = new AnotherSecurityFilter();
    }

    @Test
    void doFilterInternalSkipsLogicWhenHeaderIsMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        anotherSecurityFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    void doFilterInternalThrowsJwtExceptionWhenHeaderIsIncorrect() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user_id", "invalid");
        request.addHeader("role", "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(WrongHeaderException.class, () ->
                anotherSecurityFilter.doFilterInternal(request, response, filterChain));
    }

    @Test
    void doFilterInternalNormalFlow() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user_id", "123");
        request.addHeader("role", "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();

        anotherSecurityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getPrincipal());
        assertNotNull(authentication.getAuthorities());
    }
}