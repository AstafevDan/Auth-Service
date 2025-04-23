package com.dan.authservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Класс конфигурации свойств Kafka.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Component
public class KafkaPropertiesConfig {

    /**
     * Сервер брокера Kafka.
     */
    @Value("${kafka.bootstrap-servers}")
    private String BROKER_URL;

    /**
     * Задает свойства продюсеру Kafka.
     *
     * @return свойства {@link Properties}
     */
    public Properties createProducerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_URL);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    /**
     * Задает свойства админа Kafka.
     *
     * @return свойства {@link Properties}
     */
    public Properties createAdminProperties() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_URL);
        return props;
    }
}
