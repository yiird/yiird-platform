package com.yiird.spring.boot.autoconfigure.data.datasource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DruidWallConfig {

    private Boolean enable = false;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 对被认为是攻击的SQL进行LOG.error输出
     */
    private Boolean logViolation = false;

    /**
     * 对被认为是攻击的SQL抛出SQLException
     */
    private Boolean throwException = true;


    private Config config;

    @Getter
    @Setter
    static class Config {

        /**
         * 是否允许调用Connection.getMetadata方法，这个方法调用会暴露数据库的表信息
         */
        private Boolean metadataAllow = true;

        /**
         * 是否允许调用Connection/Statement/ResultSet的isWrapFor和unwrap方法，这两个方法调用，使得有办法拿到原生驱动的对象，绕过WallFilter的检测直接执行SQL。
         */
        private Boolean wrapAllow = true;
    }
}
