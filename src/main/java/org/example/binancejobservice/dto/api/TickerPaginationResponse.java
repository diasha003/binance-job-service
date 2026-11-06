package org.example.binancejobservice.dto.api;

import org.example.binancejobservice.entity.Ticker;

import java.util.List;

public class TickerPaginationResponse {

    private final List<Ticker> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final boolean hasNext;

    public TickerPaginationResponse(List<Ticker> items, int page, int size, long totalElements, boolean hasNext) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }

    public List<Ticker> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }


    public boolean isHasNext() {
        return hasNext;
    }

}
