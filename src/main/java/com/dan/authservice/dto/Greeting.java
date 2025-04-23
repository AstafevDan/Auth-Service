package com.dan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO, представляющая приветствие.
 *
 * @param content содержимое приветствия.
 * @author Даниил Астафьев
 * @version 1.0
 */
@Schema(description = "Сущность тестового эндпоинта с приветствием")
public record Greeting(@Schema(description = "Содержимое") String content) {
}
