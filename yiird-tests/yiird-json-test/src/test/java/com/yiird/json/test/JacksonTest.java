package com.yiird.json.test;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiird.json.test.pojo.EnumObject;
import com.yiird.json.test.pojo.SomeThing;
import com.yiird.json.test.pojo.User;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

@JsonTest
public class JacksonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private SomeThing someThing;

    @BeforeEach
    public void before() {

//        objectMapper.setDateFormat(SimpleDateFormat.getDateInstance())

        someThing = new SomeThing();
        someThing.setBool(true);
        someThing.setMBool(false);
        someThing.setDate(new Date());
        someThing.setTimestamp(new Timestamp(System.currentTimeMillis()));
        someThing.setInstant(Instant.now());
        someThing.setLocalDate(LocalDate.now());
        someThing.setLocalTime(LocalTime.now());
        someThing.setLocalDateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        someThing.setZonedDateTime(ZonedDateTime.now());
        someThing.setYear(Year.now());
        someThing.setMonth(Month.NOVEMBER);
        someThing.setPeriod(Period.ofDays(2));
        someThing.setDuration(Duration.ofDays(2));
        someThing.setString("时间");
        someThing.setDayOfWeek(DayOfWeek.MONDAY);
        someThing.setEnumObject(EnumObject.DOWN);
        someThing.setUser(new User());
    }

    @Test
    public void toJson() throws JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(someThing));
    }

    @Test
    public void toObject() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(someThing);
        System.out.println(json);
        SomeThing _someThing = objectMapper.readValue(json, SomeThing.class);
        System.out.println(_someThing);
    }

}
