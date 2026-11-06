package org.example.binancejobservice.dto.api;

import java.math.BigDecimal;

public class AvgPriceResponse {

    private final BigDecimal averagePrice;

    public AvgPriceResponse(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
}
