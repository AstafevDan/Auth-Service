package com.dan.authservice.repository;

import com.dan.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link User}.
 * Предоставляет стандартные CRUD-операции благодаря наследованию от {@link JpaRepository},
 * а также дополнительные методы для поиска пользователей.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты
     * @return {@link Optional}, содержащий пользователя, если найден,
     * или пустой {@link Optional}, если пользователь не существует
     */
    Optional<User> findByEmail(String email);
}
