package org.example.binancejobservice.dto;

import java.math.BigDecimal;

public class TickerDto {

    private int id;
    private String symbol;
    private BigDecimal price;
    private String fetchedAt ;



    public TickerDto(int id, String symbol, BigDecimal price, String fetchedAt ) {
        this.id = id;
        this.symbol = symbol;
        this.price = price;
        this.fetchedAt  = fetchedAt ;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }


    public String getFetchedAt() {
        return fetchedAt;
    }
}
