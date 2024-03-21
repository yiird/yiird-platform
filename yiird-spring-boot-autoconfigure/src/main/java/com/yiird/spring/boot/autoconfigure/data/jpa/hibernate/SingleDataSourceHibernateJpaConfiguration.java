package com.yiird.spring.boot.autoconfigure.data.jpa.hibernate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HibernateProperties.class)
@ImportRuntimeHints(HibernateRuntimeHints.class)
public class SingleDataSourceHibernateJpaConfiguration extends JpaBaseConfiguration {

    private final HibernateProperties hibernateProperties;

    private final HibernateDefaultDdlAutoProvider defaultDdlAutoProvider;

    private final DataSourcePoolMetadataProvider poolMetadataProvider;

    private final List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

    SingleDataSourceHibernateJpaConfiguration(
        ObjectProvider<DataSource> dataSource, ObjectProvider<JpaProperties> jpaProperties,
        ConfigurableListableBeanFactory beanFactory, ObjectProvider<JtaTransactionManager> jtaTransactionManager,
        HibernateProperties hibernateProperties,
        ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders,
        ObjectProvider<SchemaManagementProvider> providers,
        ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy,
        ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy,
        ObjectProvider<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        super(dataSource.getIfAvailable(), jpaProperties.getIfAvailable(), jtaTransactionManager);
        this.hibernateProperties = hibernateProperties;
        this.defaultDdlAutoProvider = new HibernateDefaultDdlAutoProvider(providers);
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(metadataProviders.getIfAvailable());
        this.hibernatePropertiesCustomizers = HibernateCommon.determineHibernatePropertiesCustomizers(
            physicalNamingStrategy.getIfAvailable(), implicitNamingStrategy.getIfAvailable(), beanFactory,
            hibernatePropertiesCustomizers.orderedStream().toList());
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        Supplier<String> defaultDdlMode = () -> this.defaultDdlAutoProvider.getDefaultDdlAuto(getDataSource());
        return new LinkedHashMap<>(this.hibernateProperties.determineHibernateProperties(
            getProperties().getProperties(), new HibernateSettings().ddlAuto(defaultDdlMode)
                .hibernatePropertiesCustomizers(this.hibernatePropertiesCustomizers)));
    }

    @Override
    protected void customizeVendorProperties(Map<String, Object> vendorProperties) {
        super.customizeVendorProperties(vendorProperties);
        if (!vendorProperties.containsKey(HibernateCommon.JTA_PLATFORM)) {
            configureJtaPlatform(vendorProperties);
        }
        if (!vendorProperties.containsKey(HibernateCommon.PROVIDER_DISABLES_AUTOCOMMIT)) {
            configureProviderDisablesAutocommit(vendorProperties);
        }
    }

    private void configureJtaPlatform(Map<String, Object> vendorProperties) throws LinkageError {
        JtaTransactionManager jtaTransactionManager = getJtaTransactionManager();
        // Make sure Hibernate doesn't attempt to auto-detect a JTA platform
        if (jtaTransactionManager == null) {
            vendorProperties.put(HibernateCommon.JTA_PLATFORM, HibernateCommon.getNoJtaPlatformManager());
        }
        // As of Hibernate 5.2, Hibernate can fully integrate with the WebSphere
        // transaction manager on its own.
        else if (!HibernateCommon.runningOnWebSphere()) {
            HibernateCommon.configureSpringJtaPlatform(vendorProperties, jtaTransactionManager);
        }
    }

    private void configureProviderDisablesAutocommit(Map<String, Object> vendorProperties) {
        if (isDataSourceAutoCommitDisabled() && !isJta()) {
            vendorProperties.put(HibernateCommon.PROVIDER_DISABLES_AUTOCOMMIT, "true");
        }
    }

    private boolean isDataSourceAutoCommitDisabled() {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(getDataSource());
        return poolMetadata != null && Boolean.FALSE.equals(poolMetadata.getDefaultAutoCommit());
    }
}
