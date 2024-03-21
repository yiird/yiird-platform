package com.yiird.spring.boot.autoconfigure.data.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
public class DruidFilterConfig {

    @NestedConfigurationProperty
    private DruidStatConfig stat;

    @NestedConfigurationProperty
    private DruidWallConfig wall;
}
