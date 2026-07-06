package com.taskflow_api.shared;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.TimeZone;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryTest {

    @BeforeAll
    static void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("taskflow_test")
            .withUsername("test")
            .withPassword("test")
            .withEnv("TZ", "UTC")
            .withEnv("PGTZ", "UTC");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
        registry.add("spring.datasource.hikari.connection-init-sql", () -> "SET TIME ZONE 'UTC'");
        registry.add("spring.flyway.init-sqls", () -> "SET TIME ZONE 'UTC'");
    }
}