package com.yiird.spring.boot.autoconfigure.data.jpa;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.yiird.spring.boot.autoconfigure.data.datasource.MultiDataSourceConditional;
import com.yiird.spring.boot.autoconfigure.data.datasource.SingleDataSourceConditional;
import com.yiird.spring.boot.autoconfigure.data.datasource.properties.DataSourceProperties;
import com.yiird.spring.boot.autoconfigure.data.jpa.hibernate.MultiDataSourceHibernateJpaConfiguration;
import com.yiird.spring.boot.autoconfigure.data.jpa.hibernate.SingleDataSourceHibernateJpaConfiguration;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Slf4j
@AutoConfiguration(
    after = {DataSourceAutoConfiguration.class, TransactionManagerCustomizationAutoConfiguration.class},
    before = org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class)
@ConditionalOnClass({AtomikosDataSourceBean.class, LocalContainerEntityManagerFactoryBean.class, EntityManager.class, SessionImplementor.class})
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class, DataSourceProperties.class})
public class HibernateJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter(JpaProperties properties) {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(properties.isShowSql());
        if (properties.getDatabase() != null) {
            adapter.setDatabase(properties.getDatabase());
        }
        if (properties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(properties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(properties.isGenerateDdl());
        return adapter;
    }

    @Configuration(proxyBeanMethods = false)
    @Conditional(MultiDataSourceConditional.class)
    @Import({MultiDataSourceHibernateJpaConfiguration.class})
    static class MultiDataSourceConfig {

    }

    @Configuration(proxyBeanMethods = false)
    @Conditional(SingleDataSourceConditional.class)
    @Import(SingleDataSourceHibernateJpaConfiguration.class)
    static class SingleDataSourceConfig {

    }
}
