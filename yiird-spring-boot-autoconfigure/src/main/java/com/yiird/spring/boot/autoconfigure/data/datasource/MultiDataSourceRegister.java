package com.yiird.spring.boot.autoconfigure.data.datasource;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.yiird.spring.boot.autoconfigure.common.EnvUtil;
import com.yiird.spring.boot.autoconfigure.common.PropertiesKeyConstants;
import com.yiird.spring.boot.autoconfigure.data.datasource.properties.DataSourceProperties;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

@Slf4j
public class MultiDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void registerBeanDefinitions(
        @NonNull AnnotationMetadata metadata,
        @NonNull BeanDefinitionRegistry registry) {
        DataSourceProperties properties = Binder.get(this.environment)
            .bind(PropertiesKeyConstants.DATASOURCE_PROPERTY, DataSourceProperties.class).get();
        properties.getSources().forEach((key, source) -> {
            source.setName(key);
            BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
                DataSource.class,
                () -> {
                    if (Objects.isNull(source.getDruid())) {
                        source.setDruid(properties.getDruid());
                    }
                    //noinspection resource
                    DruidXADataSource dataSource = new DruidDataSourceWrapper(source);
                    dataSource.setName(key);
                    return EnvUtil.isJta(environment) ? dataSource : dataSource.cloneDruidDataSource();
                });
            definitionBuilder.setPrimary(source.isPrimary());
            registry.registerBeanDefinition(key, definitionBuilder.getBeanDefinition());
        });
    }


    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
