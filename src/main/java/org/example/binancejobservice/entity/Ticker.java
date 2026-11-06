package org.example.binancejobservice.entity;

import jakarta.persistence.*;
import org.example.binancejobservice.dto.TickerDto;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Table(name = "tickers")
@Entity
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "mins")
    private int mins;

    @Column(name = "price", precision = 19, scale = 8)
    private BigDecimal price;

    @Column(name = "closeTime")
    private long closeTime;

    public Ticker (){

    }

    public Ticker(int mins, BigDecimal price, long closeTime) {
        this.mins = mins;
        this.price = price;
        this.closeTime = closeTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        this.mins = mins;
    }

    public BigDecimal  getPrice() {
        return price;
    }

    public void setPrice(BigDecimal  price) {
        this.price = price;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }


    public TickerDto toDto(){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedCloseTime = df.format(new Date(closeTime));

        return new TickerDto(
                id,
                "BNBUSDT",
                price,
                formattedCloseTime

        );
    }

}
