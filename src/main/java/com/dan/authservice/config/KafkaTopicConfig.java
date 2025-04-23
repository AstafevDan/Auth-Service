package com.dan.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Класс конфигурации топиков Kafka.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Создает новый топик с названием users.
     *
     * @return настроенный топик Kafka.
     */
    @Bean
    public NewTopic usersTopic() {
        return TopicBuilder.name("users")
                .build();
    }
}
