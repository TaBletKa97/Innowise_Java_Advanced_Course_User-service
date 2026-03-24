package com.innowise.userservice.security;

import io.jsonwebtoken.JwtException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_INVALID = "Bearer invalid";
    private static final String BEARER_VALID = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpZCI6MSwicm9sZSI6IkFETUlOIiwidHlwZSI6ImFjY2Vzc190b2tlbiIsImlhdCI6MTc3NDM2NDQ5NywiZXhwIjoyMDMzNTY0NDk3fQ.ejvT_2stYgce1cPMSlDcaTDlf4POXLw_9GtC3mjIqh0";

    @Mock
    private FilterChain filterChain;

    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        JwtTokenUtils tokenUtils = new JwtTokenUtils();
        String secret = "super-secret-key-at-least-32-characters-long!!";
        ReflectionTestUtils.setField(tokenUtils, "secret", secret);
        ReflectionTestUtils.invokeMethod(tokenUtils, "init");

        jwtRequestFilter = new JwtRequestFilter(tokenUtils);
    }

    @Test
    void doFilterInternalSkipsLogicWhenHeaderIsMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    void doFilterInternalThrowsJwtExceptionWhenHeaderIsIncorrect() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, BEARER_INVALID);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(JwtException.class, () ->
                jwtRequestFilter.doFilterInternal(request, response, filterChain));
    }

    @Test
    void doFilterInternalNormalFlow() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, BEARER_VALID);
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getPrincipal());
        assertNotNull(authentication.getAuthorities());
    }

}