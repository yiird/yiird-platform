package com.yiird.spring.boot.autoconfigure.data.datasource.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonDataSourceProperties {

    /**
     * 配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。如果没有配置，将会生成一个名字，格式是："DataSource-" + System.identityHashCode(this).
     */
    private String name;

    /**
     * 连接数据库的url，不同数据库不一样。例如： mysql : jdbc:mysql://10.20.153.104:3306/druid2 oracle : jdbc:oracle:thin:@10.20.149.85:1521:ocnauto
     */
    private String url;
    /**
     * 用户名
     */
    private String username;

    private String password;

    /**
     * 这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName
     */
    private String driverClassName;

    /**
     * 是否是主要的数据源，如果是多数据源，至少有一个是主要的
     */
    private boolean primary = false;
}
