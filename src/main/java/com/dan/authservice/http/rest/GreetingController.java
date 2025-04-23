package com.dan.authservice.http.rest;

import com.dan.authservice.dto.Greeting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Тестовый контроллер, который выдает приветствие пользователю. Доступ к его эндпоинту защищен JWT.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@RestController
@Tag(name = "Greeting Controller", description = "Пробный контроллер, к которому можно получить доступ только после успешной аутентификации")
public class GreetingController {

    private static final String template = "Hello, %s!";

    /**
     * Пробный эндпоинт, доступ к которому можно получить, только введя токен доступа. Позволяет получить строку с приветствием.
     *
     * @param name содержимое приветствия
     * @return объект типа {@link Greeting}
     */
    @Operation(
            summary = "Приветствие",
            description = "Позволяет получить строку с приветствием",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение строки с приветствием")
            }
    )
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(defaultValue = "World") String name) {
        return new Greeting(String.format(template, name));
    }
}
