package com.dan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO, представляющая ответ в виде jwt токена после успешной аутентификации пользователя.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность для jwt токена")
public class AuthenticationResponse {

    /**
     * JWT токен доступа.
     */
    @Schema(description = "JWT токен доступа")
    private String token;
}
