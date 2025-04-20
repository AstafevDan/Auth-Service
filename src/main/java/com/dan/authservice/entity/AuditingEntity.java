package com.dan.authservice.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * Абстрактный класс сущности с поддержкой аудита.
 * Реализует интерфейс {@link BaseEntity} и включает поля для отслеживания времени создания
 * и последнего изменения сущности.
 *
 * @param <T> тип идентификатора, который должен реализовать {@link Serializable}
 * @author Даниил Астафьев
 * @version 1.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditingEntity<T extends Serializable> implements BaseEntity<T> {

    /**
     * Время создания сущности.
     * Автоматически устанавливается при создании записи.
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Время последнего изменения сущности.
     * Автоматически обновляется при изменении записи.
     */
    @LastModifiedDate
    private Instant lastModifiedAt;
}
