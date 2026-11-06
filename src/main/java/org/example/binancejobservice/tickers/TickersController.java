package org.example.binancejobservice.tickers;

import org.example.binancejobservice.dto.TickerDto;
import org.example.binancejobservice.dto.api.AvgPriceResponse;
import org.example.binancejobservice.dto.api.TickerPaginationResponse;
import org.example.binancejobservice.dto.api.TrendResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/")
public class TickersController {

   private final TickersService tickersService;

    public TickersController(TickersService tickersService) {
        this.tickersService = tickersService;
    }

    @GetMapping("prices/latest")
    public TickerDto getTickersLatestPrices (){
        return tickersService.getTickersLatestPrices();
    }


    @GetMapping("/prices/average")
    public AvgPriceResponse getTickersAveragePrices (
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to){

        return  tickersService.getTickersAveragePrices(from, to);

    }

    @GetMapping("/prices")
    public TickerPaginationResponse getTickersHistoryByPeriod(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size){

        return tickersService.getTickersHistoryByPeriod(from, to, page, size);

    }

    @GetMapping("/prices/trend")
    public TrendResponse getTickerTrendByPeriod(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to){

        return tickersService.getTickerTrendByPeriod(from, to);

    }

}
