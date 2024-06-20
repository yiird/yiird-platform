package com.yiird.spring.boot.autoconfigure.data.filestorage.search;

import java.time.Instant;

public class RangeFilter implements SearchFilter {
    private Instant begin;
    private Instant end;

    private final TimeStrategy strategy;

    public RangeFilter(Instant instant, TimeStrategy strategy) {
        this.strategy = strategy;
        if (TimeStrategy.AFTER == strategy) {
            this.end = instant;
        } else if (TimeStrategy.BEFORE == strategy) {
            this.begin = instant;
        }
    }

    public RangeFilter(Instant begin, Instant end) {
        this.begin = begin;
        this.end = end;
        this.strategy = TimeStrategy.RANGE;
    }


    @Override
    public boolean filter(Object instant) {

        if (instant instanceof Instant _instant) {
            return switch (strategy) {
                case AFTER -> _instant.isAfter(end);
                case BEFORE -> _instant.isBefore(begin);
                case RANGE -> _instant.isAfter(begin) && _instant.isBefore(end);
            };
        }
        return false;
    }
}
