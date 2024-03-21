package com.yiird.spring.boot.autoconfigure.data.datasource.properties;

import com.yiird.spring.boot.autoconfigure.data.datasource.DruidFilterConfig;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
public class DruidSampleProperties {
    /**
     * 初始化时建立物理连接的个数。
     */
    private Integer initialSize = 0;

    /**
     * 最大连接池数量
     */
    private Integer maxActive = 8;

    /**
     * 最小连接池数量
     */
    private Integer minIdle = 0;

    /**
     * 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
     */
    private Long maxWait = -1L;

    /**
     * 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
     */
    private Boolean poolPreparedStatements = false;

    /**
     * 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
     */
    private Integer maxPoolPreparedStatementPerConnectionSize = -1;

    /**
     * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
     */
    private String validationQuery;

    /**
     * 单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法
     */
    private Integer validationQueryTimeout = -1;

    /**
     * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
     */
    private Boolean testOnBorrow = false;

    /**
     * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
     */
    private Boolean testOnReturn = false;

    /**
     * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
     */
    private Boolean testWhileIdle = true;

    /**
     * 连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作
     */
    private Boolean keepAlive = false;
    /**
     * 有两个含义： 1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。 2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明 默认：1分钟
     */
    private Long timeBetweenEvictionRunsMillis = 60000L;
    /**
     * 连接保持空闲而不被驱逐的最小时间 连接数>minIdle 并且 空闲时间>minEvictableIdleTimeMillis关闭空闲连接 默认：30分钟
     */
    private Long minEvictableIdleTimeMillis = 1800000L;
    /**
     * 空闲时间>maxEvictableIdleTimeMillis，不管连接池中的连接数是否>minIdle都关闭连接 默认：7小时
     */
    private Long maxEvictableIdleTimeMillis = 25200000L;

       /* @NestedConfigurationProperty
        private JpaProperties jpa;
*/
    /**
     * 物理连接初始化的时候执行的sql
     */
    private List<String> connectionInitSqls;

    private String filters;

    @NestedConfigurationProperty
    private DruidFilterConfig filter;
}
