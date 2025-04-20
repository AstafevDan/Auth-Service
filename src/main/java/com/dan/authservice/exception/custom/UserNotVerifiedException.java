package com.dan.authservice.exception.custom;

/**
 * Исключение, наследующееся от {@link RuntimeException}.
 * Возникает, если пользователь хочет пройти аутентификацию и получить токен доступа без верификации аккаунта.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
public class UserNotVerifiedException extends RuntimeException {

    /**
     * Конструктор для создания исключения.
     *
     * @param message сообщение о возникшем исключении.
     */
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
