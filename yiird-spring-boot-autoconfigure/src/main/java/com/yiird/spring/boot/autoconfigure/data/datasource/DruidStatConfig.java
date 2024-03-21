package com.yiird.spring.boot.autoconfigure.data.datasource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DruidStatConfig {

    private Boolean enable = false;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     *
     */
    private Boolean mergeSql = false;
    /**
     * 开启慢SQL记录功能
     */
    private Boolean logSlowSql;
    /**
     * 单位毫秒 默认：3秒
     */
    private Long slowSqlMillis;
}
