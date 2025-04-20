package com.dan.authservice.http.controller.auth;

import com.dan.authservice.dto.*;
import com.dan.authservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Контроллер для управления регистрацией, аутентификацией и верификации email пользователя.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Tag(name = "Authentication Controller", description = "Контроллер для управления аутентификацией пользователя")
public class AuthenticationController {

    /**
     * URL эндпоинта, которому обращается контроллер при отправке кода подтверждения.
     */
    public static final String URL = "http://localhost:8089/api/v1/codes/verify";

    private final AuthenticationService service;
    private final RestTemplate restTemplate;

    /**
     * Позволяет зарегистрировать нового пользователя.
     *
     * @param request данные для регистрации (username, email, password)
     * @return Http статус - ACCEPTED (202)
     */
    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать нового пользователя. Отправляет сообщение сервису рассылки для получения верификационного кода",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные для регистрации")
            }
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Позволяет пользователю аутентифицироваться.
     *
     * @param request данные для аутентификации (email, password)
     * @return сгенерированный JWT токен
     */
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Позволяет пользователю войти в систему. Возвращает access токен",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аутентификация прошла успешно"),
                    @ApiResponse(responseCode = "401", description = "Некорректные учетные данные"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не верифицирован")
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * Позволяет верифицировать аккаунт пользователя через ввод кода подтверждения.
     * <p>Процесс верификации:
     * <ol>
     *   <li>Проверяет валидность кода подтверждения через внешний сервис</li>
     *   <li>При успешной проверке отмечает email как подтвержденный в системе</li>
     * </ol>
     *
     * @param request данные для верификации (email, code)
     * @return ответ от сервиса, занимающегося проверкой кода, с флагом isValid
     */
    @Operation(
            summary = "Верификация пользователя",
            description = "Позволяет верифицировать аккаунт пользователя через ввод кода подтверждения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Верификация прошла успешно"),
                    @ApiResponse(responseCode = "400", description = "Верификация не пройдена"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "409", description = "Аккаунт пользователя уже верифицирован")
            }
    )
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @RequestBody @Valid CodeVerificationRequest request
    ) {
        ResponseEntity<CodeVerificationResponse> response = restTemplate.postForEntity(URL, request, CodeVerificationResponse.class);
        if (response.hasBody() && Objects.requireNonNull(response.getBody()).isValid()) {
            service.verifyEmail(request.getEmail());
            return ResponseEntity.ok(response.getBody());
        }
        return ResponseEntity.badRequest().build();
    }
}
