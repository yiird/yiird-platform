package com.yiird.spring.boot.autoconfigure.common;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum CaseFormat {

    /**
     * 蛇形命名法.<br>例如：`user_name`, `total_sales`, `customer_address`
     */
    SNAKE("[a-z][a-z0-9]+(_[a-z0-9]+)*"),
    /**
     * 大写形命名法.<br>例如：`USER_NAME`, `TOTAL_SALES`, `CUSTOMER_ADDRESS`
     */
    UPPER_SNAKE("[A-Z][A-Z0-9]*(_[A-Z0-9]+)*"),
    /**
     * 驼峰命名法.<br>例如：`firstName`, `totalAmount`, `phoneNumber`
     */
    CAMEL("[a-z][a-z0-9]+([A-Z][a-z0-9]*)*"),
    /**
     * 帕斯卡命名法.<br>例如：`EmployeeName`, `CarModel`, `ProductID`
     */
    PASCAL("[A-Z][a-zA-Z0-9]*"),
    /**
     * 肉串命名法.<br>例如：`my-css-class`, `page-title`, `section-header`
     */
    KEBAB("[a-z][a-z0-9]+(-[a-z0-9]+)*"),
    /**
     * 大写肉串命名法.<br>例如：`MY-CSS-CLASS`, `PAGE-TITLE`, `SECTION-HEADER`
     */
    UPPER_KEBAB("[A-Z][A-Z0-9]+(-[A-Z0-9]+)*");

    private final String regex;
    private String text;

    private CaseFormat setText(String text) {
        this.text = text;
        return this;
    }

    CaseFormat(String regex) {
        this.regex = regex;
    }

    public boolean check(String text) {
        return Pattern.matches(this.regex, text);
    }

    /**
     * 判断字符串属于那种命名法
     * <p>
     * 例如：{@code CaseFormat.which("firstName")} 返回: {@code CaseFormat.CAMEL}
     *
     * @param text 输入字符串
     * @return 命名方法枚举
     */
    public static CaseFormat which(String text) {
        if (Objects.nonNull(text) && !text.isBlank()) {
            CaseFormat format = null;
            if (SNAKE.check(text)) {
                format = CaseFormat.SNAKE;
            } else if (UPPER_SNAKE.check(text)) {
                format = CaseFormat.UPPER_SNAKE;
            } else if (CAMEL.check(text)) {
                format = CaseFormat.CAMEL;
            } else if (PASCAL.check(text)) {
                format = CaseFormat.PASCAL;
            } else if (KEBAB.check(text)) {
                format = CaseFormat.KEBAB;
            } else if (UPPER_KEBAB.check(text)) {
                format = CaseFormat.UPPER_KEBAB;
            }
            if(format == null){
                System.out.println();
            }
            return Objects.requireNonNull(format).setText(text);
        } else {
            throw new IllegalArgumentException("text 不能为空");
        }

    }

    private String toSnake(String text) {

        if (!check(text)) {
            throw new IllegalArgumentException("字符串必须为" + name() + "格式");
        } else {
            return switch (this) {
                case SNAKE -> text;
                case UPPER_SNAKE -> text.toLowerCase();
                case CAMEL, PASCAL -> {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < text.length(); i++) {
                        char currentChar = text.charAt(i);
                        if (i > 0 && Character.isUpperCase(currentChar)) {
                            builder.append('_');
                        }
                        builder.append(Character.toLowerCase(currentChar));
                    }
                    yield builder.toString();
                }
                case KEBAB, UPPER_KEBAB -> text.toLowerCase().replace("-", "_");
            };
        }
    }

    private String clearSb(StringBuilder builder) {
        String str = builder.toString();
        builder.delete(0, str.length());
        return str;
    }

    private String snakeTo(String text, CaseFormat format) {
        return switch (format) {
            case SNAKE -> text;
            case UPPER_SNAKE -> text.toUpperCase();
            case CAMEL, PASCAL -> {
                String[] items = text.split("_");
                StringBuilder builder = new StringBuilder();
                for (String item : items) {
                    char firstChat = item.charAt(0);
                    char upperChar = Character.toUpperCase(firstChat);
                    if (firstChat != upperChar) {
                        char[] chars = item.toCharArray();
                        chars[0] = upperChar;
                        builder.append(new String(chars));
                    } else {
                        builder.append(item);
                    }
                }
                if (CAMEL.equals(format)) {
                    char[] chars = builder.toString().toCharArray();
                    chars[0] = Character.toLowerCase(chars[0]);
                    yield new String(chars);
                } else {
                    yield builder.toString();
                }

            }
            case KEBAB -> text.replace("_", "-");
            case UPPER_KEBAB -> text.replace("_", "-").toUpperCase();
        };
    }

    public String to(CaseFormat format) {
        return to(this.text, format);
    }

    public String to(String text, CaseFormat format) {
        try {
            return snakeTo(toSnake(text), format);
        } catch (RuntimeException e) {
            log.error("{}与当前类型[{}]不符，无法进行转换", text, this.name(), e);
            return text;
        }
    }

}
