package org.example.binancejobservice.logic.managers;

import jakarta.transaction.Transactional;
import org.example.binancejobservice.dto.api.TickerBinanceApi;
import org.example.binancejobservice.entity.Ticker;
import org.example.binancejobservice.repository.TickerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.logging.Logger;


@Component
@EnableScheduling
public class BinanceManager {

    private final RestClient restClient;
    private final TickerRepository tickerRepository;

    private static final Logger LOGGER =
            Logger.getLogger(BinanceManager.class.getName());

    @Value("${api.binance.base-url}")
    private String baseUrl;


    public BinanceManager(RestClient restClient, TickerRepository tickerRepository) {
        this.restClient = restClient;
        this.tickerRepository = tickerRepository;
    }

    @Scheduled(fixedDelayString = "${BINANCE_POLL_INTERVAL}")
    public void getTickersLatestPrices (){

        LOGGER.info("obtaining average price of ticker ");

        TickerBinanceApi tickerApi = restClient.get()
                .uri(baseUrl + "v3/avgPrice?symbol=BNBUSDT")
                .retrieve()
                .onStatus(status -> status.value() == 429 || status.value() == 418, (request, response) -> {
                    String retryAfterHeader = response.getHeaders().getFirst("Retry-After");
                    int retryAfterSeconds = retryAfterHeader != null ? Integer.parseInt(retryAfterHeader) : 60;


                    System.err.println("Binance limit exceeded! We’re waiting for seconds: " + retryAfterSeconds);

                    try {
                        Thread.sleep(retryAfterSeconds * 1000L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    throw new RuntimeException("Rate limit reached, waited " + retryAfterSeconds + "s");
                })
                .toEntity(TickerBinanceApi.class)
                .getBody();

        LOGGER.info("saving data to database ");
        tickerRepository.save(
                new Ticker(tickerApi.getMins(),
                        tickerApi.getPrice(),
                        tickerApi.getClose_time())
        );

    }
}
