package com.dan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO, представляющая запрос для аутентификации пользователя.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность для аутентификации пользователя")
public class AuthenticationRequest {

    /**
     * Email пользователя.
     */
    @NotBlank(message = "Email is required to specify")
    @Email(message = "Email should be valid")
    @Schema(description = "Email пользователя", example = "test@test.com")
    private String email;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Password is required to specify")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Пароль пользователя")
    private String password;
}
