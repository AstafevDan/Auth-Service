package com.dan.authservice.entity;

import java.io.Serializable;

/**
 * Интерфейс для сущностей с уникальным идентификатором.
 *
 * @param <T> тип идентификатора, который должен реализовать {@link Serializable}
 * @author Даниил Астафьев
 * @version 1.0
 */
public interface BaseEntity<T extends Serializable> {

    /**
     * Возвращает уникальный идентификатор сущности.
     *
     * @return идентификатор сущности
     */
    T getId();

    /**
     * Устанавливает уникальный идентификатор сущности.
     *
     * @param id идентификатор для установки
     */
    void setId(T id);
}
