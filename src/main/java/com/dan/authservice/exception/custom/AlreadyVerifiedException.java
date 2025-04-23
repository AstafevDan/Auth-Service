package com.dan.authservice.exception.custom;

/**
 * Исключение, наследующееся от {@link RuntimeException}.
 * Возникает, если пользователь хочет верифицировать аккаунт, хотя до этого уже был верифицирован.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
public class AlreadyVerifiedException extends RuntimeException {

    /**
     * Конструктор для создания исключения.
     *
     * @param message сообщение о возникшем исключении.
     */
    public AlreadyVerifiedException(String message) {
        super(message);
    }
}
