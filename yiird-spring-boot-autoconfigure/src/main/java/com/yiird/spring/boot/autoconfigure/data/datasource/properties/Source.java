package com.yiird.spring.boot.autoconfigure.data.datasource.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
public class Source extends CommonDataSourceProperties{

    @NestedConfigurationProperty
    private DruidSampleProperties druid;
}
