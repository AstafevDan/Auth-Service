package com.dan.authservice.integration.controller;

import com.dan.authservice.dto.AuthenticationRequest;
import com.dan.authservice.dto.CodeVerificationRequest;
import com.dan.authservice.dto.CodeVerificationResponse;
import com.dan.authservice.dto.RegistrationRequest;
import com.dan.authservice.integration.IntegrationTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@AutoConfigureMockMvc
public class AuthenticationControllerIT extends IntegrationTestBase {

    private static final String VERIFICATION_URL = "http://localhost:8089/api/v1/codes/verify";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void registerUser() throws Exception {
        RegistrationRequest request = RegistrationRequest.builder()
                .username("user4")
                .email("test4@email.com")
                .password("password4")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void authenticateUser_Success() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test2@email.com")
                .password("password2")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void authenticateUser_UserNotVerified() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test1@email.com")
                .password("password1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void verifyEmail_Success() throws Exception {
        CodeVerificationRequest codeVerificationRequest = CodeVerificationRequest.builder()
                .email("test1@email.com")
                .code("123456")
                .build();

        CodeVerificationResponse codeVerificationResponse = CodeVerificationResponse.builder()
                .isValid(true)
                .build();

        String responseJson = objectMapper.writeValueAsString(codeVerificationResponse);

        mockServer.expect(ExpectedCount.once(),
                        requestTo(VERIFICATION_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codeVerificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        mockServer.verify();
    }

    @Test
    void verifyEmail_InvalidCode() throws Exception {
        CodeVerificationRequest codeVerificationRequest = CodeVerificationRequest.builder()
                .email("test1@email.com")
                .code("111111")
                .build();

        CodeVerificationResponse codeVerificationResponse = CodeVerificationResponse.builder()
                .isValid(false)
                .build();

        String responseJson = objectMapper.writeValueAsString(codeVerificationResponse);

        mockServer.expect(ExpectedCount.once(),
                        requestTo(VERIFICATION_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codeVerificationRequest)))
                .andExpect(status().isBadRequest());

        mockServer.verify();
    }
}
