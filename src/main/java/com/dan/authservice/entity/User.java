package com.dan.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

/**
 * Класс сущности пользователя, наследующий класс {@link AuditingEntity} и реализующий интерфейсы {@link UserDetails} и {@link Principal}.
 * Содержит такие базовые поля, как id, username, email, password, emailVerified.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "email", callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends AuditingEntity<Long> implements UserDetails, Principal {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя (никнейм).
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Электронная почта пользователя.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Пароль пользователя.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Логическое поле, определяющие верифицирован ли пользователь.
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }

    @Override
    public String getName() {
        return email;
    }
}
