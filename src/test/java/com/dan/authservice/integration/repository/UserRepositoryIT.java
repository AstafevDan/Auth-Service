package com.dan.authservice.integration.repository;

import com.dan.authservice.entity.User;
import com.dan.authservice.integration.IntegrationTestBase;
import com.dan.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class UserRepositoryIT extends IntegrationTestBase {

    private final UserRepository userRepository;

    private static final String USER_EMAIL = "test1@email.com";
    private static final String NON_EXISTED_EMAIL = "test@test.test";

    @Test
    void findByEmail_ShouldReturnUser() {
        Optional<User> optionalUser = userRepository.findByEmail(USER_EMAIL);

        assertTrue(optionalUser.isPresent());
        assertThat(optionalUser.get().getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    void findByEmail_ShouldReturnEmptyOptional() {
        Optional<User> optionalUser = userRepository.findByEmail(NON_EXISTED_EMAIL);

        assertFalse(optionalUser.isPresent());
    }
}
