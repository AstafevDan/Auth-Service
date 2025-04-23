package com.dan.authservice.exception.custom;

/**
 * Исключение, наследующееся от {@link RuntimeException}.
 * Возникает, если создание топика Kafka было завершено с ошибкой.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
public class TopicCreationException extends RuntimeException {

    /**
     * Конструктор для создания исключения.
     *
     * @param msg сообщение о возникшем исключении.
     * @param e   исключение или ошибка
     */
    public TopicCreationException(String msg, Throwable e) {
        super(msg, e);
    }
}
