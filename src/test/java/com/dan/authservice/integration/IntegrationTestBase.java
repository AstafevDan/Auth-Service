package com.dan.authservice.integration;

import com.dan.authservice.integration.annotation.IT;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@IT
@Sql("classpath:sql/data.sql")
public abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeAll
    static void startContainer() {
        postgres.start();
        kafka.start();
    }
}
