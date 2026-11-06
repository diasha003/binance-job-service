package org.example.binancejobservice.tickers;

import org.example.binancejobservice.dto.TickerDto;
import org.example.binancejobservice.dto.api.AvgPriceResponse;
import org.example.binancejobservice.dto.api.TickerPaginationResponse;
import org.example.binancejobservice.dto.api.TrendResponse;
import org.example.binancejobservice.entity.Ticker;
import org.example.binancejobservice.repository.TickerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
public class TickersService {

    private final TickerRepository tickerRepository;

    public TickersService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    public TickerDto getTickersLatestPrices() {
        Ticker ticker = tickerRepository.findTopByOrderByCloseTimeDesc()
                .orElseThrow(() -> new RuntimeException("There are no records"));

        return ticker.toDto();
    }

    public AvgPriceResponse getTickersAveragePrices(Date fromDate, Date toDate){

        BigDecimal avgPrice = tickerRepository.getAvgPriceByTimePeriod(fromDate.getTime(), toDate.getTime())
                .orElseThrow(() -> new RuntimeException("There are no records"));

        return new AvgPriceResponse(avgPrice);
    }

    public TickerPaginationResponse getTickersHistoryByPeriod(Date fromDate, Date toDate, Integer page, Integer size){

        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        var pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNumber);

        Page<Ticker> tickers = tickerRepository.getTickersByTimePeriodAndPagination(fromDate.getTime(), toDate.getTime(), pageable);
        return new TickerPaginationResponse(tickers.toList(), pageNumber, pageSize, tickers.getTotalElements(), tickers.hasNext());

    }

    public TrendResponse getTickerTrendByPeriod(Date fromDate, Date toDate){

        List<Ticker> tickers = tickerRepository.findFirstAndLastByTimePeriod(fromDate.getTime(), toDate.getTime());

        BigDecimal firstPrice = tickers.get(0).getPrice();
        BigDecimal lastPrice = tickers.get(tickers.size()-1).getPrice();

        String trend;
        int comparison = lastPrice.compareTo(firstPrice);

        if (comparison > 0) {
            trend = "UPWARD";
        } else if (comparison < 0) {
            trend = "DOWNWARD";
        } else {
            trend = "FLAT";
        }

        String pattern = "yyyy-dd-mm";
        DateFormat df = new SimpleDateFormat(pattern);

        return new TrendResponse("BNBUSDT", df.format(fromDate), df.format(toDate), trend);


    }

}
