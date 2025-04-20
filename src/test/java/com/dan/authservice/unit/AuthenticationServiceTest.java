package com.dan.authservice.unit;

import com.dan.authservice.dto.AuthenticationRequest;
import com.dan.authservice.dto.AuthenticationResponse;
import com.dan.authservice.dto.RegistrationRequest;
import com.dan.authservice.entity.User;
import com.dan.authservice.exception.custom.AlreadyVerifiedException;
import com.dan.authservice.exception.custom.UserNotVerifiedException;
import com.dan.authservice.repository.UserRepository;
import com.dan.authservice.service.AuthenticationService;
import com.dan.authservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthenticationService.class)
public class AuthenticationServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    private static final String username = "username1";
    private static final String email = "email1@test.com";
    private static final String password = "password1";
    private static final String encryptedPassword = "encryptedPassword1";

    @Test
    void registerUser() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        when(passwordEncoder.encode(password)).thenReturn(encryptedPassword);

        authenticationService.register(registrationRequest);

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
        verify(kafkaTemplate).send(anyString(), eq(email));
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();
        assertEquals(email, user.getEmail());
        assertEquals(encryptedPassword, user.getPassword());
        assertFalse(user.getEmailVerified());
    }

    @Test
    void authenticateUser_Success() {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();
        User user = User.builder()
                .username(username)
                .email(email)
                .emailVerified(true)
                .build();
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(jwtService.generateToken(user)).thenReturn("jwtToken1");

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals("jwtToken1", response.getToken());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(user);
    }

    @Test
    void authenticateUser_NotVerified() {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();
        User user = User.builder()
                .username(username)
                .email(email)
                .emailVerified(false)
                .build();
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(user, null));

        assertThrows(UserNotVerifiedException.class, () -> authenticationService.authenticate(authenticationRequest));
    }

    @Test
    void verifyEmail_Success() {
        User user = User.builder()
                .username(username)
                .email(email)
                .emailVerified(false)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authenticationService.verifyEmail(email);

        assertTrue(user.getEmailVerified());
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(user);
    }

    @Test
    void verifyEmail_UserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.verifyEmail(email));
    }

    @Test
    void verifyEmail_EmailAlreadyVerified() {
        User user = User.builder()
                .username(username)
                .email(email)
                .emailVerified(true)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(AlreadyVerifiedException.class, () -> authenticationService.verifyEmail(email));
    }
}
