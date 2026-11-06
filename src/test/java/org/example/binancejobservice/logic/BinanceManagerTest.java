package org.example.binancejobservice.logic;

import org.example.binancejobservice.entity.Ticker;
import org.example.binancejobservice.logic.managers.BinanceManager;
import org.example.binancejobservice.repository.TickerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class BinanceManagerTest {

    private static final String BASE_URL = "https://binance.test/api/";
    private static final String AVG_PRICE_URL = BASE_URL + "v3/avgPrice?symbol=BNBUSDT";

    @Mock
    private TickerRepository tickerRepository;

    private MockRestServiceServer server;
    private BinanceManager manager;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        manager = new BinanceManager(builder.build(), tickerRepository);
        ReflectionTestUtils.setField(manager, "baseUrl", BASE_URL);
    }

    @Test
    void fetchesAveragePriceAndPersistsTicker() {
        server.expect(requestTo(AVG_PRICE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"mins\":5,\"price\":\"612.34567890\",\"closeTime\":1700000000000}",
                        MediaType.APPLICATION_JSON));

        manager.getTickersLatestPrices();

        ArgumentCaptor<Ticker> saved = ArgumentCaptor.forClass(Ticker.class);
        verify(tickerRepository).save(saved.capture());
        assertThat(saved.getValue().getMins()).isEqualTo(5);
        assertThat(saved.getValue().getPrice()).isEqualByComparingTo("612.34567890");
        assertThat(saved.getValue().getCloseTime()).isEqualTo(1_700_000_000_000L);
        server.verify();
    }

    @Test
    void rateLimitResponseFailsWithoutSaving() {
        server.expect(requestTo(AVG_PRICE_URL))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS).header(HttpHeaders.RETRY_AFTER, "0"));

        assertThatThrownBy(() -> manager.getTickersLatestPrices())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Rate limit reached, waited 0s");

        verify(tickerRepository, never()).save(any());
    }

    @Test
    void ipBanResponseFailsWithoutSaving() {
        server.expect(requestTo(AVG_PRICE_URL))
                .andRespond(withStatus(HttpStatus.valueOf(418)).header(HttpHeaders.RETRY_AFTER, "0"));

        assertThatThrownBy(() -> manager.getTickersLatestPrices())
                .hasMessage("Rate limit reached, waited 0s");

        verify(tickerRepository, never()).save(any());
    }

    @Test
    void serverErrorFailsWithoutSaving() {
        server.expect(requestTo(AVG_PRICE_URL))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> manager.getTickersLatestPrices())
                .isInstanceOf(RuntimeException.class);

        verify(tickerRepository, never()).save(any());
    }
}
