package com.yiird.spring.boot.autoconfigure.data.jpa.hibernate;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.yiird.spring.boot.autoconfigure.data.datasource.properties.DataSourceProperties;
import com.yiird.spring.boot.autoconfigure.data.jpa.properties.JpaPackageProperties;
import com.yiird.spring.boot.autoconfigure.data.jpa.properties.JpaPackageProperties.ModulePackage;
import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@ImportRuntimeHints(HibernateRuntimeHints.class)
@ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class, EntityManager.class, SessionImplementor.class})
public class MultiDataSourceHibernateJpaConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Binder binder;
    private final Map<String, DataSource> sources = new HashMap<>();

    private JpaProperties properties;

    private JpaPackageProperties packageProperties;
    private HibernateProperties hibernateProperties;
    private DataSourceProperties dataSourceProperties;

    private JtaTransactionManager jtaTransactionManager;
    private HibernateDefaultDdlAutoProvider defaultDdlAutoProvider;

    private DataSourcePoolMetadataProvider poolMetadataProvider;

    private List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;
    private DefaultListableBeanFactory factory;


    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {
        this.factory = (DefaultListableBeanFactory) registry;
        this.properties = binder.bind("spring.jpa", JpaProperties.class).orElseGet(JpaProperties::new);
        this.packageProperties = binder.bind("spring.jpa", JpaPackageProperties.class).orElseGet(JpaPackageProperties::new);
        this.hibernateProperties = binder.bind("spring.jpa.hibernate", HibernateProperties.class).orElseGet(HibernateProperties::new);
        this.dataSourceProperties = binder.bind("spring.datasource", DataSourceProperties.class).orElseGet(DataSourceProperties::new);

        this.jtaTransactionManager = this.factory.getBeanProvider(JtaTransactionManager.class).getIfAvailable();

        ObjectProvider<SchemaManagementProvider> providers = this.factory.getBeanProvider(SchemaManagementProvider.class);
        this.defaultDdlAutoProvider = new HibernateDefaultDdlAutoProvider(providers);

        ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders = this.factory.getBeanProvider(DataSourcePoolMetadataProvider.class);
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(metadataProviders.stream().collect(Collectors.toList()));

        ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy = this.factory.getBeanProvider(PhysicalNamingStrategy.class);
        ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy = this.factory.getBeanProvider(ImplicitNamingStrategy.class);
        ObjectProvider<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers = this.factory.getBeanProvider(HibernatePropertiesCustomizer.class);

        this.hibernatePropertiesCustomizers = HibernateCommon.determineHibernatePropertiesCustomizers(physicalNamingStrategy.getIfAvailable(),
            implicitNamingStrategy.getIfAvailable(),
            this.factory,
            hibernatePropertiesCustomizers.orderedStream().toList());

        this.extractDataSource();
        this.createEntityManagerFactories();
    }

    private void extractDataSource() {
        dataSourceProperties.getSources().forEach((key, source) -> {
            if (isJta()) {
                DruidXADataSource xaDataSource = this.factory.getBean(key, DruidXADataSource.class);
                AtomikosDataSourceBean dataSource = binder.bind("spring.jta.atomikos.datasource", AtomikosDataSourceBean.class).orElseGet(AtomikosDataSourceBean::new);
                dataSource.setXaDataSource(xaDataSource);
                dataSource.setUniqueResourceName(key);
                //使用druid连接池配置覆盖Atomikos的连接池配置
                dataSource.setMaxPoolSize(xaDataSource.getMaxActive());
                dataSource.setMinPoolSize(xaDataSource.getMinIdle());
                dataSource.setMaintenanceInterval(Long.valueOf(xaDataSource.getTimeBetweenEvictionRunsMillis() / 1000).intValue());
                dataSource.setMaxIdleTime(Long.valueOf(xaDataSource.getMinEvictableIdleTimeMillis() / 1000).intValue());
                dataSource.setMaxLifetime(Long.valueOf(xaDataSource.getMaxEvictableIdleTimeMillis() / 1000).intValue());
                if (-1L != xaDataSource.getMaxWait()) {
                    if (xaDataSource.getMaxWait() < 100) {
                        log.warn("Druid连接池，获取连接最大等待时间小于100ms，选择使用Atomikos默认配置(borrowConnectionTimeout = 30s)");
                    } else {
                        dataSource.setBorrowConnectionTimeout(Long.valueOf(xaDataSource.getMaxWait() / 1000).intValue());
                    }
                }
                sources.put(key, dataSource);
            } else {
                DruidDataSource dataSource = this.factory.getBean(key, DruidDataSource.class);
                sources.put(key, dataSource);
            }
        });
    }


    private void createEntityManagerFactories() {
        Map<String, ModulePackage> pkg = this.packageProperties.map();
        if (isJta()) {
            dataSourceProperties.getSources().forEach((key, sourceProperties) -> {
                Map<String, Object> vendorProperties = getVendorProperties(key);
                customizeVendorProperties(key, vendorProperties);
                BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
                    LocalContainerEntityManagerFactoryBean.class,
                    () -> {
                        ObjectProvider<PersistenceUnitManager> persistenceUnitManager = this.factory.getBeanProvider(PersistenceUnitManager.class);
                        ObjectProvider<EntityManagerFactoryBuilderCustomizer> builderCustomizers = this.factory.getBeanProvider(EntityManagerFactoryBuilderCustomizer.class);
                        ObjectProvider<JpaVendorAdapter> jpaVendorAdapter = this.factory.getBeanProvider(JpaVendorAdapter.class);
                        EntityManagerFactoryBuilder entityManagerFactoryBuilder = entityManagerFactoryBuilder(this.properties, jpaVendorAdapter,
                            persistenceUnitManager, builderCustomizers);
                        return entityManagerFactoryBuilder.dataSource(getDataSource(key))
//                    .managedTypes(persistenceManagedTypes)
                            .properties(vendorProperties)
                            .mappingResources(getMappingResources())
                            .packages(pkg.get(key).getRepositoryScanPackages().toArray(new String[0]))
                            .persistenceUnit(key + "PersistenceUnit")
                            .jta(isJta())
                            .build();
                    });
                definitionBuilder.setPrimary(sourceProperties.isPrimary());
                definitionBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                factory.registerBeanDefinition(key + "EntityManagerFactory", definitionBuilder.getBeanDefinition());
            });
        } else {
            // TODO 非jta处理
        }

    }

    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
        JpaProperties properties,
        ObjectProvider<JpaVendorAdapter> jpaVendorAdapter,
        ObjectProvider<PersistenceUnitManager> persistenceUnitManager,
        ObjectProvider<EntityManagerFactoryBuilderCustomizer> customizers) {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(jpaVendorAdapter.getIfAvailable(), properties.getProperties(),
            persistenceUnitManager.getIfAvailable());
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder;
    }

    private String[] getMappingResources() {
        List<String> mappingResources = this.properties.getMappingResources();
        return (!ObjectUtils.isEmpty(mappingResources) ? StringUtils.toStringArray(mappingResources) : null);
    }


    protected Map<String, Object> getVendorProperties(String sourceKey) {
        Supplier<String> defaultDdlMode = () -> this.defaultDdlAutoProvider.getDefaultDdlAuto(getDataSource(sourceKey));
        return new LinkedHashMap<>(this.hibernateProperties.determineHibernateProperties(
            this.properties.getProperties(), new HibernateSettings().ddlAuto(defaultDdlMode)
                .hibernatePropertiesCustomizers(this.hibernatePropertiesCustomizers)));
    }

    protected void customizeVendorProperties(
        String sourceKey, Map<String, Object> vendorProperties) {
        if (!vendorProperties.containsKey(HibernateCommon.JTA_PLATFORM)) {
            configureJtaPlatform(vendorProperties);
        }
        if (!vendorProperties.containsKey(HibernateCommon.PROVIDER_DISABLES_AUTOCOMMIT)) {
            configureProviderDisablesAutocommit(sourceKey, vendorProperties);
        }
    }

    private void configureProviderDisablesAutocommit(
        String sourceKey, Map<String, Object> vendorProperties) {
        if (isDataSourceAutoCommitDisabled(sourceKey) && !isJta()) {
            vendorProperties.put(HibernateCommon.PROVIDER_DISABLES_AUTOCOMMIT, "true");
        }
    }

    protected final boolean isJta() {
        return (this.jtaTransactionManager != null);
    }


    private boolean isDataSourceAutoCommitDisabled(String sourceKey) {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(getDataSource(sourceKey));
        return poolMetadata != null && Boolean.FALSE.equals(poolMetadata.getDefaultAutoCommit());
    }

    private DataSource getDataSource(String sourceKey) {
        return this.sources.get(sourceKey);
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

    protected JtaTransactionManager getJtaTransactionManager() {
        return this.jtaTransactionManager;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.binder = Binder.get(environment);
    }

}
