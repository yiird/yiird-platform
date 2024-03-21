package com.yiird.spring.boot.autoconfigure.data.datasource.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource", ignoreInvalidFields = true)
public class DataSourceProperties extends Source {

    /**
     * 数据源配置
     */
    @NestedConfigurationProperty
    private Map<String, Source> sources;
}
