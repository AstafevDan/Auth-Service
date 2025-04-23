package com.dan.authservice.service;

import com.dan.authservice.dto.AuthenticationRequest;
import com.dan.authservice.dto.AuthenticationResponse;
import com.dan.authservice.dto.RegistrationRequest;
import com.dan.authservice.entity.User;
import com.dan.authservice.exception.custom.AlreadyVerifiedException;
import com.dan.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления регистрацией, аутентификацией и верификацией по коду подтверждения пользователя.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    /**
     * Название топика Kafka.
     */
    private static final String TOPIC_NAME = "users";

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer<Long, String> kafkaProducer;

    /**
     * Регистрирует нового пользователя в системе.
     * <p>Выполняет:
     * <ul>
     *   <li>Хеширование пароля</li>
     *   <li>Сохранение пользователя в БД</li>
     *   <li>Отправку события в Kafka для подтверждения email</li>
     * </ul>
     *
     * @param request данные для регистрации (username, email, password)
     */
    @Transactional
    public void register(RegistrationRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .build();
        User savedUser = userRepository.save(user);

        final ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC_NAME, savedUser.getId(), savedUser.getEmail());
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Send failed for record {} because {}", record, exception.getMessage());
            } else {
                log.info("Sent record {}", record);
            }
        });
    }

    /**
     * Аутентифицирует пользователя и генерирует JWT токен.
     *
     * @param request данные для аутентификации (email, password)
     * @return ответ с JWT токеном ({@link AuthenticationResponse})
     * @throws DisabledException если email пользователя не подтвержден
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) auth.getPrincipal();
        if (!user.getEmailVerified()) {
            throw new DisabledException("User " + user.getEmail() + " not verified");
        }

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Подтверждает email пользователя.
     *
     * @param email email пользователя для подтверждения
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AlreadyVerifiedException  если email уже подтвержден
     */
    @Transactional
    public void verifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));

        if (user.getEmailVerified()) {
            throw new AlreadyVerifiedException("User " + email + " is already verified");
        }

        user.setEmailVerified(true);

        userRepository.save(user);
    }
}
