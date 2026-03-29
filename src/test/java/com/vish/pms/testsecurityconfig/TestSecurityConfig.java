package com.vish.pms.testsecurityconfig;

import java.util.UUID;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.vish.pms.config.CustomUserDetails;
import com.vish.pms.entity.User;

@TestConfiguration class TestSecurityConfig {

        @Bean
        public UserDetailsService userDetailsService() {
            return username -> {
                User user = new User();
                user.setId(UUID.randomUUID()); // set an ID
                user.setName("Test User");
                user.setEmail("test@example.com");
                return new CustomUserDetails(user);
            };
        }
    }