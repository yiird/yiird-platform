package com.yiird.spring.boot.autoconfigure.data.filestorage.search;

import java.time.Instant;

public class ModifyTimeFilter extends RangeFilter {


    public ModifyTimeFilter(Instant instant, TimeStrategy strategy) {
        super(instant, strategy);
    }

    public ModifyTimeFilter(Instant begin, Instant end) {
        super(begin, end);
    }
}
