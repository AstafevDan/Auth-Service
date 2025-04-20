package com.dan.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * Класс, содержащий конфигурацию основных бинов приложения.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    /**
     * Бин, предназначенный для загрузки данных специфичных для конкретного пользователя.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Создаёт и настраивает провайдер аутентификации, использующий {@link DaoAuthenticationProvider}.
     * Устанавливает сервис для получения данных пользователя и кодировщик паролей.
     *
     * @return настроенный {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Создаёт кодировщик паролей, использующий алгоритм BCrypt.
     *
     * @return {@link PasswordEncoder} на основе BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создаёт менеджер аутентификации, получаемый из конфигурации Spring Security.
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return {@link AuthenticationManager} для обработки аутентификации
     * @throws Exception если не удалось получить менеджер аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Создаёт экземпляр {@link RestTemplate} для выполнения HTTP-запросов к REST API.
     *
     * @return настроенный {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
