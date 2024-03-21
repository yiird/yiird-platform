package com.yiird.spring.boot.autoconfigure.data.jpa.config;

import com.yiird.spring.boot.autoconfigure.common.PropertiesKeyConstants;
import com.yiird.spring.boot.autoconfigure.data.jpa.properties.JpaPackageProperties;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationUtils;
import org.springframework.util.Assert;

public class JpaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport implements ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(
        AnnotationMetadata metadata, BeanDefinitionRegistry registry,
        BeanNameGenerator generator) {

        Assert.notNull(metadata, "AnnotationMetadata must not be null");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");

        // Guard against calls for sub-classes
        if (metadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }

        JpaPackageProperties properties = Binder.get(this.environment)
            .bind(PropertiesKeyConstants.JPA_PROPERTY, JpaPackageProperties.class).get();

        properties.getPackages().forEach((jpaPackage) -> {
            AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(metadata,
                getAnnotation(), resourceLoader, environment, registry, generator);
            AnnotationAttributes attributes = configurationSource.getAttributes();
            attributes.put("entityManagerFactoryRef", jpaPackage.getDataSourceKey() + "EntityManagerFactory");
            attributes.put("basePackages", jpaPackage.getRepositoryScanPackages().toArray(new String[0]));

            RepositoryConfigurationExtension extension = getExtension();
            RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

            RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader,
                environment);

            delegate.registerRepositoriesIn(registry, extension);
        });
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJpaRepositoriesInternal.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new JpaRepositoryConfigExtension();
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
