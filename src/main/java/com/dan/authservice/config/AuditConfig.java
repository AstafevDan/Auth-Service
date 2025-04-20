package com.dan.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Конфигурация для поддержки аудита сущностей.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {
}
