package com.dan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO, предназначенная для описания пользователю возникшего исключения/ошибки.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Сущность для описания пользователю возникшей ошибки")
public class ErrorInformationResponse {

    /**
     * Сообщение с описанием возникшей ошибки.
     */
    @Schema(description = "Сообщение с описанием возникшей ошибки")
    private final String message;
}
