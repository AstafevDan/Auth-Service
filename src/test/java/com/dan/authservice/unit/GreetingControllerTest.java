package com.dan.authservice.unit;

import com.dan.authservice.config.SecurityConfig;
import com.dan.authservice.exception.JwtAuthenticationEntryPoint;
import com.dan.authservice.filter.JwtAuthenticationFilter;
import com.dan.authservice.http.rest.GreetingController;
import com.dan.authservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GreetingController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class GreetingControllerTest {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private JwtAuthenticationEntryPoint jwtEntryPoint;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            String authHeader = request.getHeader("Authorization");
            if ((authHeader != null && authHeader.startsWith("Bearer ")) ||
                    SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(request, response);
                return null;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                any(FilterChain.class));
    }

    @Test
    @WithMockUser
    void greetingWithValidToken() throws Exception {
        mockMvc.perform(get("/greeting")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello, John!"));
    }

    @Test
    void greetingWithoutToken() throws Exception {
        mockMvc.perform(get("/greeting"))
                .andExpect(status().isUnauthorized());
    }
}
