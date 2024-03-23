package com.yiird.data.test;


import com.yiird.data.test.pojo.SomeThing;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

@JsonTest
public class JacksonTest {

    private SomeThing someThing;

    @BeforeEach
    public void before() {
        someThing = new SomeThing();
        someThing.setBool(true);
        someThing.setMBool(false);
        someThing.setDate(new Date(System.currentTimeMillis()));
        someThing.setLocalDate(LocalDate.now());
        someThing.setLocalTime(LocalTime.now());
        someThing.setLocalDateTime(LocalDateTime.now());
        someThing.setYear(Year.now());
        someThing.setMonth(Month.NOVEMBER);
        someThing.setString("时间");
        someThing.setDayOfWeek(DayOfWeek.MONDAY);
    }

    @Test
    public void objectToJson() {
    }

}
