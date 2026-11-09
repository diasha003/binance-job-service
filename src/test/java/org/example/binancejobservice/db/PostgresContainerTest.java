package org.example.binancejobservice.db;

import org.example.binancejobservice.entity.Ticker;
import org.example.binancejobservice.repository.TickerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class PostgresContainerTest {

    @Container
    private static PostgreSQLContainer
            postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private TickerRepository tickerRepository;

    @Test
    void testSaveUserManualContainer() {
        Ticker ticker = new Ticker();

        ticker.setMins(5);
        ticker.setPrice(BigDecimal.valueOf(681.43511657));
        ticker.setCloseTime(1794223019069L);

        Ticker savedTicker = tickerRepository.save(ticker);
        assertThat(savedTicker.getId()).isNotNull();
        assertThat(savedTicker.getMins()).isEqualTo(5);

    }
}