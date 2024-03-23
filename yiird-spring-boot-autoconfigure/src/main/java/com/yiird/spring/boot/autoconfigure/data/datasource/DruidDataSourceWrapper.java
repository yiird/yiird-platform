package com.yiird.spring.boot.autoconfigure.data.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.wall.WallFilter;
import com.yiird.spring.boot.autoconfigure.data.datasource.DruidWallConfig.Config;
import com.yiird.spring.boot.autoconfigure.data.datasource.properties.DruidSampleProperties;
import com.yiird.spring.boot.autoconfigure.data.datasource.properties.Source;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class DruidDataSourceWrapper extends DruidXADataSource implements InitializingBean {

    public DruidDataSourceWrapper(Source source) {
        super();
        this.setName(source.getName());
        this.setUrl(source.getUrl());
        this.setUsername(source.getUsername());
        this.setPassword(source.getPassword());
        this.setDriverClassName(source.getDriverClassName());
        setParameters(source.getDruid());
    }

    private void setParameters(DruidSampleProperties properties) {
        if (Objects.isNull(properties)) {
            log.warn("未设置数据源[{}]连接池，因为Druid配置为空", this.getName());
            return;
        }
        this.setInitialSize(properties.getInitialSize());
        this.setMaxActive(properties.getMaxActive());
        this.setMinIdle(properties.getMinIdle());
        this.setMaxWait(properties.getMaxWait());

        if (-1L != properties.getMaxWait()) {
            //获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁
            super.setUseUnfairLock(true);
        }

        this.setPoolPreparedStatements(properties.getPoolPreparedStatements());
        this.setMaxPoolPreparedStatementPerConnectionSize(
            properties.getMaxPoolPreparedStatementPerConnectionSize());
        this.setValidationQuery(properties.getValidationQuery());
        this.setValidationQueryTimeout(properties.getValidationQueryTimeout());
        this.setTestOnBorrow(properties.getTestOnBorrow());
        this.setTestOnReturn(properties.getTestOnReturn());
        this.setTestWhileIdle(properties.getTestWhileIdle());
        this.setKeepAlive(properties.getKeepAlive());
        this.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        this.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        this.setMaxEvictableIdleTimeMillis(properties.getMaxEvictableIdleTimeMillis());
        this.setConnectionInitSqls(properties.getConnectionInitSqls());
        try {
            this.setFilters(properties.getFilters());
        } catch (SQLException e) {
            log.error("设置druid过滤器错误", e);
        }

        DruidFilterConfig filter = properties.getFilter();

        if (Objects.nonNull(filter)) {
            List<Filter> filters = new ArrayList<>();
            DruidStatConfig statConfig = filter.getStat();
            if (statConfig.getEnable()) {
                filters.add(createStatFilter(statConfig));
            }

            DruidWallConfig wallConfig = filter.getWall();
            if (wallConfig.getEnable()) {
                filters.add(createWallFilter(wallConfig));
            }

            super.filters.addAll(filters);
        }
    }

    private StatFilter createStatFilter(DruidStatConfig config) {
        StatFilter filter = new StatFilter();
        filter.setDbType(config.getDbType());
        filter.setMergeSql(config.getMergeSql());
        filter.setLogSlowSql(config.getLogSlowSql());
        filter.setSlowSqlMillis(config.getSlowSqlMillis());
        return filter;
    }

    private WallFilter createWallFilter(DruidWallConfig config) {
        WallFilter filter = new WallFilter();
        filter.setDbType(config.getDbType());
        filter.setLogViolation(config.getLogViolation());
        filter.setThrowException(config.getThrowException());

        Config wconfig = config.getConfig();

        com.alibaba.druid.wall.WallConfig _config = new com.alibaba.druid.wall.WallConfig();
        _config.setMetadataAllow(wconfig.getMetadataAllow());
        _config.setWrapAllow(wconfig.getWrapAllow());

        filter.setConfig(_config);

        return filter;
    }


    @Override
    public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
        try {
            super.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        } catch (IllegalArgumentException ignore) {
            super.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.init();
    }
}
