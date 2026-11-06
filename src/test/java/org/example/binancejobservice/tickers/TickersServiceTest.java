package org.example.binancejobservice.tickers;

import org.example.binancejobservice.dto.TickerDto;
import org.example.binancejobservice.dto.api.AvgPriceResponse;
import org.example.binancejobservice.dto.api.TickerPaginationResponse;
import org.example.binancejobservice.dto.api.TrendResponse;
import org.example.binancejobservice.entity.Ticker;
import org.example.binancejobservice.repository.TickerRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TickersServiceTest {

    private static final Date FROM = new Date(1_700_000_000_000L);
    private static final Date TO = new Date(1_700_003_600_000L);

    @Mock
    private TickerRepository tickerRepository;

    private TickersService service() {
        return new TickersService(tickerRepository);
    }

    @Test
    void latestPriceReturnsDtoOfNewestTicker() {
        Ticker ticker = new Ticker(5, new BigDecimal("601.5"), 1_700_000_000_000L);
        when(tickerRepository.findTopByOrderByCloseTimeDesc()).thenReturn(Optional.of(ticker));

        TickerDto dto = service().getTickersLatestPrices();

        assertThat(dto.getSymbol()).isEqualTo("BNBUSDT");
        assertThat(dto.getPrice()).isEqualByComparingTo("601.5");
        assertThat(dto.getFetchedAt()).isEqualTo("2023-11-14T22:13:20Z");
    }

    @Test
    void latestPriceThrowsWhenNoRecords() {
        when(tickerRepository.findTopByOrderByCloseTimeDesc()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().getTickersLatestPrices())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There are no records");
    }

    @Test
    void averagePriceQueriesRepositoryWithEpochMillis() {
        when(tickerRepository.getAvgPriceByTimePeriod(any(), any()))
                .thenReturn(Optional.of(new BigDecimal("600.25")));

        AvgPriceResponse response = service().getTickersAveragePrices(FROM, TO);

        assertThat(response.getAveragePrice()).isEqualByComparingTo("600.25");
        verify(tickerRepository).getAvgPriceByTimePeriod(FROM.getTime(), TO.getTime());
    }

    @Test
    void averagePriceThrowsWhenNoRecordsInPeriod() {
        when(tickerRepository.getAvgPriceByTimePeriod(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().getTickersAveragePrices(FROM, TO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There are no records");
    }

    @Test
    void historyUsesProvidedPageAndSize() {
        List<Ticker> items = List.of(new Ticker(5, BigDecimal.ONE, 1L), new Ticker(5, BigDecimal.TEN, 2L));
        when(tickerRepository.getTickersByTimePeriodAndPagination(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items, Pageable.ofSize(2).withPage(1), 7));

        TickerPaginationResponse response = service().getTickersHistoryByPeriod(FROM, TO, 1, 2);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(tickerRepository).getTickersByTimePeriodAndPagination(
                eq(FROM.getTime()), eq(TO.getTime()), pageable.capture());
        assertThat(pageable.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageable.getValue().getPageSize()).isEqualTo(2);

        assertThat(response.getItems()).isEqualTo(items);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(7);
        assertThat(response.isHasNext()).isTrue();
    }

    @Test
    void historyDefaultsToFirstPageOfTenWhenParamsMissing() {
        when(tickerRepository.getTickersByTimePeriodAndPagination(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        TickerPaginationResponse response = service().getTickersHistoryByPeriod(FROM, TO, null, null);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(tickerRepository).getTickersByTimePeriodAndPagination(any(), any(), pageable.capture());
        assertThat(pageable.getValue().getPageNumber()).isZero();
        assertThat(pageable.getValue().getPageSize()).isEqualTo(10);

        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getItems()).isEmpty();
        assertThat(response.isHasNext()).isFalse();
    }

    @Test
    void historyHasNextIsFalseOnLastPage() {
        List<Ticker> items = List.of(new Ticker(5, BigDecimal.ONE, 1L));
        when(tickerRepository.getTickersByTimePeriodAndPagination(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items, Pageable.ofSize(5).withPage(1), 6));

        TickerPaginationResponse response = service().getTickersHistoryByPeriod(FROM, TO, 1, 5);

        assertThat(response.getTotalElements()).isEqualTo(6);
        assertThat(response.isHasNext()).isFalse();
    }

    @ParameterizedTest(name = "first={0}, last={1} -> {2}")
    @CsvSource({
            "600.00, 650.00, UPWARD",
            "650.00, 600.00, DOWNWARD",
            "600.00, 600.00, FLAT",
            "600.0,  600.00, FLAT"
    })
    void trendComparesLastPriceWithFirst(String first, String last, String expected) {
        when(tickerRepository.findFirstAndLastByTimePeriod(any(), any())).thenReturn(List.of(
                new Ticker(5, new BigDecimal(first), 1L),
                new Ticker(5, new BigDecimal(last), 2L)));

        TrendResponse response = service().getTickerTrendByPeriod(FROM, TO);

        assertThat(response.getTrend()).isEqualTo(expected);
        assertThat(response.getSymbol()).isEqualTo("BNBUSDT");
        verify(tickerRepository).findFirstAndLastByTimePeriod(FROM.getTime(), TO.getTime());
    }

    @Test
    void trendIsFlatWhenPeriodHasSingleRecord() {
        when(tickerRepository.findFirstAndLastByTimePeriod(any(), any()))
                .thenReturn(List.of(new Ticker(5, new BigDecimal("600"), 1L)));

        assertThat(service().getTickerTrendByPeriod(FROM, TO).getTrend()).isEqualTo("FLAT");
    }

    @Test
    void trendFormatsDatesAsYearMonthDay() {
        when(tickerRepository.findFirstAndLastByTimePeriod(any(), any())).thenReturn(List.of(
                new Ticker(5, BigDecimal.ONE, 1L),
                new Ticker(5, BigDecimal.TEN, 2L)));

        TrendResponse response = service().getTickerTrendByPeriod(FROM, TO);

        assertThat(response.getStart_date()).isEqualTo("2023-11-15");
        assertThat(response.getEnd_date()).isEqualTo("2023-11-15");
    }
}
