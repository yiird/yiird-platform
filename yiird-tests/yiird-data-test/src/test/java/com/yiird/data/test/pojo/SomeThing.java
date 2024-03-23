package com.yiird.data.test.pojo;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import lombok.Data;

@Data
public class SomeThing {

    private Year year;
    private Month month;
    private DayOfWeek dayOfWeek;
    private Date date;
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private LocalTime localTime;
    private String string;
    private Boolean bool;
    private boolean mBool;
}
