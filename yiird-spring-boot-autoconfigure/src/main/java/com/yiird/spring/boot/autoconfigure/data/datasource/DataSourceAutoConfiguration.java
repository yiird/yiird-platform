package com.yiird.spring.boot.autoconfigure.data.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.yiird.spring.boot.autoconfigure.data.datasource.properties.DataSourceProperties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Primary;

@Slf4j
@AutoConfiguration(before =
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class)
public class DataSourceAutoConfiguration {


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({DruidDataSource.class})
    @Conditional(SingleDataSourceConditional.class)
    @EnableConfigurationProperties({DataSourceProperties.class})
    protected static class DatasourceConfiguration {

        @Bean
        @Primary
        @ConditionalOnMissingBean
        public DataSource dataSource(
            DataSourceProperties properties) {
            log.info("Init DruidDataSource");
            //noinspection resource
            return new DruidDataSourceWrapper(properties).cloneDruidDataSource();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({DruidDataSource.class})
    @Conditional(MultiDataSourceConditional.class)
    @Import({MultiDataSourceRegister.class})
    @EnableConfigurationProperties({DataSourceProperties.class})
    protected static class MultiDataSourceConfiguration implements ImportBeanDefinitionRegistrar {

    }
}
