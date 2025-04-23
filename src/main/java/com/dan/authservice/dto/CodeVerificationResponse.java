package com.dan.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO, представляющая ответ пользователю о том, успешно ли пройдена верификация.
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
@Schema(description = "Сущности для ответа о верификации пользователя")
public class CodeVerificationResponse {

    /**
     * Валиден ли код подтверждения от пользователя.
     */
    @Schema(description = "Валиден ли код подтверждения от пользователя")
    @JsonProperty("valid")
    private final boolean isValid;
}
