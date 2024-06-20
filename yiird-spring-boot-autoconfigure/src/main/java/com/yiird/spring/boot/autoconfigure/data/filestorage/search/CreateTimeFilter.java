package com.yiird.spring.boot.autoconfigure.data.filestorage.search;

import java.time.Instant;

public class CreateTimeFilter extends RangeFilter {

    public CreateTimeFilter(Instant instant, TimeStrategy strategy) {
        super(instant, strategy);
    }

    public CreateTimeFilter(Instant begin, Instant end) {
        super(begin, end);
    }
}
