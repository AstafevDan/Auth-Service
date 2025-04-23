package com.dan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO, представляющая запрос для верификации пользователя через код подтверждения.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Schema(description = "Сущность запроса для верификации аккаунта")
public class CodeVerificationRequest {

    /**
     * Email пользователя.
     */
    @NotBlank(message = "Email is required to specify")
    @Email(message = "Email should be valid")
    @Schema(description = "Email пользователя", example = "test@test.com")
    private final String email;

    /**
     * Код подтверждения пользователя.
     */
    @NotBlank(message = "Code is required to specify")
    @Size(min = 6, max = 6, message = "Code must be 6 digits long")
    @Schema(description = "Код подтверждения", example = "111111")
    private final String code;
}
