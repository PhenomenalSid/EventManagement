package com.example.eventmanagement.config;

import com.example.eventmanagement.exception.AuthException.ForbiddenException;
import com.example.eventmanagement.exception.CustomAccessDeniedHandler;
import com.example.eventmanagement.filter.JWTFilter;
import com.example.eventmanagement.service.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImplementation userDetailsServiceImplementation;
    private final JWTFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(UserDetailsServiceImplementation userDetailsServiceImplementation, JWTFilter jwtFilter, CustomAccessDeniedHandler accessDeniedHandler) {
        this.userDetailsServiceImplementation = userDetailsServiceImplementation;
        this.jwtFilter = jwtFilter;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/events/**").hasAnyRole("ADMIN", "ORGANIZER")
                        .requestMatchers("/users/**").hasAnyRole("ADMIN", "ORGANIZER", "PARTICIPANT")
                        .requestMatchers("/rsvp/**").hasAnyRole("ADMIN", "PARTICIPANT", "ORGANIZER")
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsServiceImplementation);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
}

//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJPcmdhbmlzZXIiLCJpYXQiOjE3Mjk5NTcwOTgsImV4cCI6MTcyOTk2MDY5OH0.JGUJ4I5Px1XMYWIvEK0ye_YjwWlTxg55jgdKUCGFjgY
//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJQYXJ0aWNpcGFudCIsImlhdCI6MTcyOTk1NzIyMiwiZXhwIjoxNzI5OTYwODIyfQ.0QiiuNO-SkhX4uvV0iE747Cj7qiZ9jcZqE_mWcc4cSM