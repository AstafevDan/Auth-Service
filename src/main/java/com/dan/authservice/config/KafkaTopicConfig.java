package com.dan.authservice.config;

import com.dan.authservice.exception.custom.TopicCreationException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Класс конфигурации топиков Kafka.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaTopicConfig {

    /**
     * Название топика Kafka.
     */
    private static final String USERS_TOPIC = "users";

    private final KafkaPropertiesConfig kafkaProperties;

    /**
     * Создает новый топик с названием users.
     */
    @PostConstruct
    public void createTopic() {
        try (AdminClient adminClient = AdminClient.create(kafkaProperties.createAdminProperties())) {
            NewTopic topic = new NewTopic(USERS_TOPIC, 1, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();
            log.info("Topic {} created successfully", USERS_TOPIC);
        } catch (ExecutionException | InterruptedException e) {
            if (e.getCause() instanceof TopicExistsException) {
                log.error("Topic {} already exists", USERS_TOPIC);
            } else {
                throw new TopicCreationException("Failed to create topic", e);
            }
        }
    }
}
