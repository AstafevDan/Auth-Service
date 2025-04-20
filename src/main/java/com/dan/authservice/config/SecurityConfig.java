package com.dan.authservice.config;

import com.dan.authservice.exception.JwtAuthenticationEntryPoint;
import com.dan.authservice.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

/**
 * Конфигурация безопасности приложения с помощью Spring Security.
 * Настраивает CORS, управление сессиями, фильтры, правила аутентификации.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;

    /**
     * Настраивает цепочку фильтров безопасности для обработки HTTP-запросов.
     * <p>
     * - Отключает CSRF-защиту.
     * - Настраивает CORS для разрешения запросов с любых источников.
     * - Определяет правила авторизации: разрешает доступ к публичным эндпоинтам (например, /auth/**, Swagger),
     * остальные запросы требуют аутентификации.
     * - Устанавливает политику управления сессиями как STATELESS (без состояния).
     * - Добавляет JWT-фильтр перед стандартным фильтром аутентификации.
     * - Настраивает обработку исключений аутентификации с использованием {@link JwtAuthenticationEntryPoint}.
     * </p>
     *
     * @param http объект {@link HttpSecurity} для конфигурации безопасности
     * @return настроенная цепочка фильтров {@link SecurityFilterChain}
     * @throws Exception если произошла ошибка при конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                                    "/auth/**",
                                    "/v2/api-docs",
                                    "v3/api-docs",
                                    "v3/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/webjars/**",
                                    "/swagger-ui.html"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtEntryPoint));

        return http.build();
    }
}
