package com.dan.authservice.unit;

import com.dan.authservice.config.SecurityConfig;
import com.dan.authservice.dto.*;
import com.dan.authservice.exception.JwtAuthenticationEntryPoint;
import com.dan.authservice.filter.JwtAuthenticationFilter;
import com.dan.authservice.http.controller.auth.AuthenticationController;
import com.dan.authservice.service.AuthenticationService;
import com.dan.authservice.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private JwtAuthenticationFilter jwtFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String email = "test123@email.com";
    private static final String password = "test1234";

    @BeforeEach
    void setUp() throws ServletException, IOException {
        doAnswer(invocationOnMock -> {
            FilterChain chain = invocationOnMock.getArgument(2);
            chain.doFilter(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    @Test
    void registerUser() throws Exception {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username("username")
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isAccepted());

        verify(authenticationService).register(any(RegistrationRequest.class));
    }

    @Test
    void authenticateUser() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token("jwtToken")
                .build();
        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(authenticationResponse);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authenticationResponse.getToken()));

        verify(authenticationService).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void verifyEmail_Success() throws Exception {
        CodeVerificationRequest codeVerificationRequest = CodeVerificationRequest.builder()
                .email(email)
                .code("123456")
                .build();
        CodeVerificationResponse codeVerificationResponse = CodeVerificationResponse.builder()
                .isValid(true)
                .build();
        when(restTemplate.postForEntity(anyString(), any(CodeVerificationRequest.class), eq(CodeVerificationResponse.class)))
                .thenReturn(ResponseEntity.ok(codeVerificationResponse));

        mockMvc.perform(post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(codeVerificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        verify(authenticationService).verifyEmail(email);
    }

    @Test
    void verifyEmail_InvalidCode() throws Exception {
        CodeVerificationRequest request = CodeVerificationRequest.builder()
                .email(email)
                .code("111111")
                .build();
        when(restTemplate.postForEntity(anyString(), any(CodeVerificationRequest.class), any()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmail_UserNotFound() throws Exception {
        CodeVerificationRequest codeVerificationRequest = CodeVerificationRequest.builder()
                .email(email)
                .code("123456")
                .build();
        CodeVerificationResponse codeVerificationResponse = CodeVerificationResponse.builder()
                .isValid(true)
                .build();
        when(restTemplate.postForEntity(anyString(), any(CodeVerificationRequest.class), eq(CodeVerificationResponse.class)))
                .thenReturn(ResponseEntity.ok(codeVerificationResponse));
        doThrow(new UsernameNotFoundException("User not found"))
                .when(authenticationService).verifyEmail(email);

        mockMvc.perform(post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(codeVerificationRequest)))
                .andExpect(status().isNotFound());
    }
}
