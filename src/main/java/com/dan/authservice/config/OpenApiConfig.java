package com.dan.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Класс конфигурации OpenAPI спецификации с настройкой защиты эндпоинтов через JWT токен.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Даниил"
                ),
                title = "Authentication Service API",
                description = "OpenApi документация для сервиса аутентификации от Даниила Астафьева",
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8088",
                        description = "Локальное окружение"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "jwtAuth"
                )
        }
)
@SecurityScheme(
        name = "jwtAuth",
        description = "JWT аутентификация",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
