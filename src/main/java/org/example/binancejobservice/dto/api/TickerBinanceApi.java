package org.example.binancejobservice.dto.api;

import java.math.BigDecimal;

public class TickerBinanceApi {

    private int mins;
    private BigDecimal price;
    private long closeTime;

    public TickerBinanceApi(){

    }

    public TickerBinanceApi(int mins, BigDecimal price, long closeTime) {
        this.mins = mins;
        this.price = price;
        this.closeTime = closeTime;
    }

    public int getMins() {
        return mins;
    }

    public BigDecimal getPrice() {
        return price;
    }


    public long getClose_time() {
        return closeTime;
    }

}
