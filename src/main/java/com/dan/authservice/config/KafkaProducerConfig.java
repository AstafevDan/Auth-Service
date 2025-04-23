package com.dan.authservice.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс конфигурации продюсера Kafka.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * Бин продюсера Kafka.
     *
     * @param properties свойства продюсера Kafka
     * @return сконфигурированный {@link KafkaProducer}
     */
    @Bean
    public KafkaProducer<Long, String> kafkaProducer(KafkaPropertiesConfig properties) {
        return new KafkaProducer<>(properties.createProducerProperties());
    }
}
