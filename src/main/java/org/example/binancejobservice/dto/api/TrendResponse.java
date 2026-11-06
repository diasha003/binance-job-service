package org.example.binancejobservice.dto.api;

public class TrendResponse {

    private final String symbol;
    private final String start_date;
    private final String end_date;
    private final String trend;

    public TrendResponse(String symbol, String start_date, String end_date, String trend) {
        this.symbol = symbol;
        this.start_date = start_date;
        this.end_date = end_date;
        this.trend = trend;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getTrend() {
        return trend;
    }
}
