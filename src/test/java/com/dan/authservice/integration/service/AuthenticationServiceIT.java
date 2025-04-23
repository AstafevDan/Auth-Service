package com.dan.authservice.integration.service;

import com.dan.authservice.dto.AuthenticationRequest;
import com.dan.authservice.dto.AuthenticationResponse;
import com.dan.authservice.dto.RegistrationRequest;
import com.dan.authservice.entity.User;
import com.dan.authservice.exception.custom.AlreadyVerifiedException;
import com.dan.authservice.integration.IntegrationTestBase;
import com.dan.authservice.repository.UserRepository;
import com.dan.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class AuthenticationServiceIT extends IntegrationTestBase {

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private KafkaConsumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    @Test
    void registerUser() {
        RegistrationRequest request = RegistrationRequest.builder()
                .username("user4")
                .email("test4@email.com")
                .password("password4")
                .build();

        authenticationService.register(request);

        Optional<User> optionalUser = userRepository.findByEmail("test4@email.com");
        assertTrue(optionalUser.isPresent());
        assertThat(optionalUser.get().getEmail()).isEqualTo("test4@email.com");
        assertFalse(optionalUser.get().getEmailVerified());

        ConsumerRecord<Long, String> record;
        try (KafkaConsumer<Long, String> consumer = createConsumer()) {
            consumer.subscribe(Collections.singletonList("users"));
            record = consumer.poll(Duration.ofSeconds(10)).records("users").iterator().next();
        }
        assertThat(record.value()).isEqualTo("test4@email.com");
    }

    @Test
    void authenticateUser_Success() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test2@email.com")
                .password("password2")
                .build();

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertThat(response).isNotNull();
    }

    @Test
    void authenticateUser_NotVerified() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test1@email.com")
                .password("password1")
                .build();

        assertThrows(DisabledException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void verifyEmail_Success() {
        authenticationService.verifyEmail("test3@email.com");

        Optional<User> verifiedUser = userRepository.findByEmail("test3@email.com");
        assertTrue(verifiedUser.isPresent());
        assertTrue(verifiedUser.get().getEmailVerified());
    }

    @Test
    void verifyEmail_UserNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.verifyEmail("test4@email.com"));
    }

    @Test
    void verifyEmail_AlreadyVerified() {
        assertThrows(AlreadyVerifiedException.class, () -> authenticationService.verifyEmail("test2@email.com"));
    }
}
