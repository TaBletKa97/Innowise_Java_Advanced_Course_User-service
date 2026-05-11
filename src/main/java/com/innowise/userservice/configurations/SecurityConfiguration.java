package com.innowise.userservice.configurations;

import com.innowise.userservice.security.AnotherSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AnotherSecurityFilter anotherSecurityFilter) {

        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .addFilterBefore(anotherSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        configurer -> configurer
                                .authenticationEntryPoint(
                                        ((request, response, authException) -> {
                                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                            response.getWriter().print("Authentication required");
                                        })
                                )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/*").permitAll()
                        .requestMatchers("/users/**", "/cards/**").authenticated()
                        .requestMatchers("/**").hasRole("ADMIN"))
                .build();
    }

}
