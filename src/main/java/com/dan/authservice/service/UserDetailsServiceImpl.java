package com.dan.authservice.service;

import com.dan.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса {@link UserDetailsService}, которая загружает данные пользователя
 * из базы данных для аутентификации.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает данные пользователя по его email.
     *
     * @param username email, идентифицирующий пользователя
     * @return объект {@link UserDetails} с данными пользователя
     * @throws UsernameNotFoundException если пользователь с указанным email не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
