package com.dan.authservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Компонент Spring Security для обработки ошибок аутентификации.
 * Реализует интерфейс {@link AuthenticationEntryPoint}, чтобы отправлять HTTP-ответ с кодом 401 (Unauthorized)
 * в случае неудачной аутентификации, возвращая JSON с информацией об ошибке.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Обрабатывает ошибку аутентификации, отправляя клиенту HTTP-ответ с кодом 401.
     * Формирует JSON-ответ, содержащий сообщение об ошибке, основанное на переданном исключении
     * {@link AuthenticationException}.
     *
     * @param request       HTTP-запрос, вызвавший ошибку аутентификации
     * @param response      HTTP-ответ для отправки клиенту
     * @param authException исключение, возникшее при аутентификации
     * @throws IOException если произошла ошибка при записи ответа
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Exception exception = (Exception) request.getAttribute("exception");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> data = new HashMap<>();
        data.put("message", exception != null ? exception.getMessage() : authException.getCause().toString());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, data);
        out.flush();
    }
}
