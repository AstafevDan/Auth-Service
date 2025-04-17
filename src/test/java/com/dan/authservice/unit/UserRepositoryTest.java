package com.dan.authservice.unit;

import com.dan.authservice.entity.User;
import com.dan.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    public static final String EMAIL = "testEmail1@test.com";

    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByEmail_whenUserExists() {
        User user = User.builder()
                .id(1L)
                .username("test1")
                .email(EMAIL)
                .password("testPassword1")
                .emailVerified(true)
                .build();
        userRepository.save(user);

        Optional<User> optionalUser = userRepository.findByEmail(EMAIL);

        assertTrue(optionalUser.isPresent());
        assertThat(optionalUser.get().getEmail()).isEqualTo(EMAIL);
    }
}
