package org.example.binancejobservice.repository;

import org.example.binancejobservice.entity.Ticker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {

    Optional<Ticker> findTopByOrderByCloseTimeDesc();

    @Query(
            value = "SELECT AVG(price) FROM tickers " +
                    "WHERE close_time BETWEEN :from AND :to", nativeQuery = true)
    Optional<BigDecimal> getAvgPriceByTimePeriod(
            @Param("from") Long fromDate,
            @Param("to") Long toDate);


    @Query(
            value = "SELECT * FROM tickers WHERE close_time BETWEEN :from AND :to ORDER BY close_time",
            countQuery = "SELECT count(*) FROM tickers WHERE close_time BETWEEN :from AND :to",
            nativeQuery = true
    )
    Page<Ticker> getTickersByTimePeriodAndPagination(
            @Param("from") Long fromDate,
            @Param("to") Long toDate,
            Pageable pageable
    );


    @Query(
            value = "WITH rankedTickers AS (\n" +
                    "  SELECT *,\n" +
                    "         ROW_NUMBER() OVER (ORDER BY close_time ASC) AS ct_asc,\n" +
                    "         ROW_NUMBER() OVER (ORDER BY close_time DESC) AS ct_desc\n" +
                    "  FROM tickers\n" +
                    "  WHERE close_time BETWEEN :from AND :to\n" +
                    ")\n" +
                    "SELECT * FROM rankedTickers\n" +
                    "WHERE ct_asc = 1 OR ct_desc = 1;",
            nativeQuery = true
    )
    List<Ticker> findFirstAndLastByTimePeriod(
            @Param("from") Long fromDate,
            @Param("to") Long toDate
    );
}
