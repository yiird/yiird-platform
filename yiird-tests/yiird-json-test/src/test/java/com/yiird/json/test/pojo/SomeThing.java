package com.yiird.json.test.pojo;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.Data;

@Data
public class SomeThing {

    private Year year;
    private Month month;
    private DayOfWeek dayOfWeek;
    private Date date;
    private Timestamp timestamp;
    private Instant instant;
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private ZonedDateTime zonedDateTime;
    private LocalTime localTime;
    private Period period;
    private Duration duration;
    private String string;
    private Boolean bool;
    private boolean mBool;
    private EnumObject enumObject;

//    @JsonUnwrapped
    private User user;

}
