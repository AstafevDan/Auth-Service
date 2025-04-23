package com.dan.authservice.http.handler;

import com.dan.authservice.dto.ErrorInformationResponse;
import com.dan.authservice.exception.custom.AlreadyVerifiedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений приложения.
 * Перехватывает исключения на уровне контроллеров и возвращает структурированные ответы с соответствующими HTTP статусами.
 * Для каждого типа исключения возвращает соответствующий HTTP статус и детали ошибки.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@RestControllerAdvice(basePackages = {
        "com.dan.authservice.http.rest",
        "com.dan.authservice.http.controller.auth"
})
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ошибки валидации входных параметров.
     *
     * @param ex исключение MethodArgumentNotValidException
     * @return ResponseEntity с Map, содержащей ошибки валидации
     * и статусом BAD_REQUEST (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach((error) -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Обрабатывает NullPointerException.
     *
     * @param ex исключение NullPointerException
     * @return ResponseEntity с {@link ErrorInformationResponse} и статусом NOT_FOUND (404)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorInformationResponse> handleNullPointer(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorInformationResponse("Object is null: " + ex.getMessage()));
    }

    /**
     * Обрабатывает ошибки аутентификации.
     *
     * @param ex исключение BadCredentialsException
     * @return ResponseEntity с {@link ErrorInformationResponse} и статусом UNAUTHORIZED (401)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorInformationResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorInformationResponse("Bad credentials: " + ex.getMessage()));
    }

    /**
     * Обрабатывает случаи, когда пользователь не найден.
     *
     * @param ex исключение UsernameNotFoundException
     * @return ResponseEntity с {@link ErrorInformationResponse} и статусом NOT_FOUND (404)
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorInformationResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorInformationResponse(ex.getMessage()));
    }

    /**
     * Обрабатывает случаи, когда email пользователя не подтвержден.
     *
     * @param ex исключение {@link DisabledException}
     * @return ResponseEntity с {@link ErrorInformationResponse} и статусом FORBIDDEN (403)
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorInformationResponse> handleUserNotVerified(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorInformationResponse(ex.getMessage()));
    }

    /**
     * Обрабатывает случаи, когда email пользователя уже подтвержден.
     *
     * @param ex исключение {@link AlreadyVerifiedException}
     * @return ResponseEntity с {@link ErrorInformationResponse} и статусом CONFLICT (409)
     */
    @ExceptionHandler(AlreadyVerifiedException.class)
    public ResponseEntity<ErrorInformationResponse> handleAlreadyVerified(AlreadyVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorInformationResponse(ex.getMessage()));
    }

    /**
     * Перехватывает все необработанные исключения.
     *
     * @param ex исключение Exception
     * @return ResponseEntity с {@link ErrorInformationResponse} и статусом INTERNAL_SERVER_ERROR (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInformationResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorInformationResponse("An error has occurred: " + ex.getMessage()));
    }
}
