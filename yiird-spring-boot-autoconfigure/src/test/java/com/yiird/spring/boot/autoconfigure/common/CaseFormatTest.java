package com.yiird.spring.boot.autoconfigure.common;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CaseFormatTest {

    private static final String[] testCollection1 = new String[]{
        "firstName", "first_name", "FIRST_NAME", "first-name", "FIRST-NAME", "FirstName"
    };


    private static final String[] testCollection2 = new String[]{
        "first2Name1", "first2_name1", "FIRST2_NAME1", "first2-name1", "FIRST2-NAME1", "First2Name1"
    };

    @Test
    public void anyToCamel() {
        String[] validationCollection = new String[]{};

        String target = "firstName";
        String target1 = "first2Name1";

        Assertions.assertAll(Arrays.stream(testCollection1).map((testCase) -> () -> Assertions.assertEquals(target, CaseFormat.which(testCase).to(CaseFormat.CAMEL))));
        Assertions.assertAll(Arrays.stream(testCollection2).map((testCase) -> () -> Assertions.assertEquals(target1, CaseFormat.which(testCase).to(CaseFormat.CAMEL))));
    }

    @Test
    public void anyToSnake() {
        String[] validationCollection = new String[]{};

        String target = "first_name";
        String target1 = "first2_name1";

        Assertions.assertAll(Arrays.stream(testCollection1).map((testCase) -> () -> Assertions.assertEquals(target, CaseFormat.which(testCase).to(CaseFormat.SNAKE))));
        Assertions.assertAll(Arrays.stream(testCollection2).map((testCase) -> () -> Assertions.assertEquals(target1, CaseFormat.which(testCase).to(CaseFormat.SNAKE))));
    }

    @Test
    public void anyToUpperSnake() {
        String[] validationCollection = new String[]{};

        String target = "FIRST_NAME";
        String target1 = "FIRST2_NAME1";

        Assertions.assertAll(Arrays.stream(testCollection1).map((testCase) -> () -> Assertions.assertEquals(target, CaseFormat.which(testCase).to(CaseFormat.UPPER_SNAKE))));
        Assertions.assertAll(Arrays.stream(testCollection2).map((testCase) -> () -> Assertions.assertEquals(target1, CaseFormat.which(testCase).to(CaseFormat.UPPER_SNAKE))));
    }

    @Test
    public void anyToPascal() {
        String[] validationCollection = new String[]{};

        String target = "FirstName";
        String target1 = "First2Name1";

        Assertions.assertAll(Arrays.stream(testCollection1).map((testCase) -> () -> Assertions.assertEquals(target, CaseFormat.which(testCase).to(CaseFormat.PASCAL))));
        Assertions.assertAll(Arrays.stream(testCollection2).map((testCase) -> () -> Assertions.assertEquals(target1, CaseFormat.which(testCase).to(CaseFormat.PASCAL))));
    }

    @Test
    public void anyToKebab() {
        String[] validationCollection = new String[]{};

        String target = "first-name";
        String target1 = "first2-name1";

        Assertions.assertAll(Arrays.stream(testCollection1).map((testCase) -> () -> Assertions.assertEquals(target, CaseFormat.which(testCase).to(CaseFormat.KEBAB))));
        Assertions.assertAll(Arrays.stream(testCollection2).map((testCase) -> () -> Assertions.assertEquals(target1, CaseFormat.which(testCase).to(CaseFormat.KEBAB))));
    }

    @Test
    public void anyToUpperKebab() {
        String[] validationCollection = new String[]{};

        String target = "FIRST-NAME";
        String target1 = "FIRST2-NAME1";

        Assertions.assertAll(Arrays.stream(testCollection1).map((testCase) -> () -> Assertions.assertEquals(target, CaseFormat.which(testCase).to(CaseFormat.UPPER_KEBAB))));
        Assertions.assertAll(Arrays.stream(testCollection2).map((testCase) -> () -> Assertions.assertEquals(target1, CaseFormat.which(testCase).to(CaseFormat.UPPER_KEBAB))));
    }


    @Test
    void testWhich() {
        Assertions.assertEquals(CaseFormat.CAMEL, CaseFormat.which("firstName"));
        Assertions.assertEquals(CaseFormat.SNAKE, CaseFormat.which("total_sales"));
        Assertions.assertEquals(CaseFormat.UPPER_SNAKE, CaseFormat.which("USER_NAME"));
        Assertions.assertEquals(CaseFormat.PASCAL, CaseFormat.which("EmployeeName"));
        Assertions.assertEquals(CaseFormat.KEBAB, CaseFormat.which("page-title"));
        Assertions.assertEquals(CaseFormat.UPPER_KEBAB, CaseFormat.which("PAGE-TITLE"));

        Assertions.assertEquals(CaseFormat.CAMEL, CaseFormat.which("first0Name1"));
        Assertions.assertEquals(CaseFormat.SNAKE, CaseFormat.which("total0_sales1"));
        Assertions.assertEquals(CaseFormat.UPPER_SNAKE, CaseFormat.which("USER0_NAME1"));
        Assertions.assertEquals(CaseFormat.PASCAL, CaseFormat.which("Employee0Name1"));
        Assertions.assertEquals(CaseFormat.KEBAB, CaseFormat.which("page0-title1"));
        Assertions.assertEquals(CaseFormat.UPPER_KEBAB, CaseFormat.which("PAGE0-TITLE1"));

    }
}