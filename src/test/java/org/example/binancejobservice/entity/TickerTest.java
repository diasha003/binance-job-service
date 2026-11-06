package org.example.binancejobservice.entity;

import org.example.binancejobservice.dto.TickerDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TickerTest {

    @Test
    void toDtoMapsPriceAndFixedSymbol() {
        Ticker ticker = new Ticker(5, new BigDecimal("612.34567890"), 1_700_000_000_000L);

        TickerDto dto = ticker.toDto();

        assertThat(dto.getSymbol()).isEqualTo("BNBUSDT");
        assertThat(dto.getPrice()).isEqualByComparingTo("612.34567890");
    }

    @Test
    void toDtoFormatsCloseTimeAsUtcIso8601() {
        Ticker ticker = new Ticker(5, BigDecimal.ONE, 1_700_000_000_000L);

        assertThat(ticker.toDto().getFetchedAt()).isEqualTo("2023-11-14T22:13:20Z");
    }

    @Test
    void toDtoFormatsEpochStart() {
        Ticker ticker = new Ticker(5, BigDecimal.ONE, 0L);

        assertThat(ticker.toDto().getFetchedAt()).isEqualTo("1970-01-01T00:00:00Z");
    }

    @Test
    void toDtoDropsMillisecondsFromCloseTime() {
        Ticker ticker = new Ticker(5, BigDecimal.ONE, 1_700_000_000_999L);

        assertThat(ticker.toDto().getFetchedAt()).isEqualTo("2023-11-14T22:13:20Z");
    }
}
