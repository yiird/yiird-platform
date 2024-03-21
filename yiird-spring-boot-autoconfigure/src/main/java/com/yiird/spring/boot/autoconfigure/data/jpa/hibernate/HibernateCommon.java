package com.yiird.spring.boot.autoconfigure.data.jpa.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ClassUtils;

@Slf4j
class HibernateCommon {


    protected static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";

    protected static final String PROVIDER_DISABLES_AUTOCOMMIT = "hibernate.connection.provider_disables_autocommit";

    /**
     * {@code NoJtaPlatform} implementations for various Hibernate versions.
     */
    protected static final String[] NO_JTA_PLATFORM_CLASSES = {
        "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform",
        "org.hibernate.service.jta.platform.internal.NoJtaPlatform"};

    /*protected static JpaProperties mergeJpaProperties(JpaProperties main, JpaProperties other) {

        if (Objects.isNull(main.getDatabase())) {
            main.setDatabase(other.getDatabase());
        }
        if (Objects.isNull(main.getOpenInView())) {
            main.setOpenInView(other.getOpenInView());
        }
        if (Objects.isNull(main.getDatabasePlatform())) {
            main.setDatabasePlatform(other.getDatabasePlatform());
        }

        if(Objects.isNull(main.getProperties())){
            main.setProperties(other.getProperties());
        }
        return main;
    }*/

    protected static Object getNoJtaPlatformManager() {
        for (String candidate : HibernateCommon.NO_JTA_PLATFORM_CLASSES) {
            try {
                return Class.forName(candidate).getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                // Continue searching
            }
        }
        throw new IllegalStateException("No available JtaPlatform candidates amongst " + Arrays.toString(HibernateCommon.NO_JTA_PLATFORM_CLASSES));
    }

    protected static void configureSpringJtaPlatform(
        Map<String, Object> vendorProperties, JtaTransactionManager jtaTransactionManager) {
        try {
            vendorProperties.put(HibernateCommon.JTA_PLATFORM, new SpringJtaPlatform(jtaTransactionManager));
        } catch (LinkageError ex) {
            // NoClassDefFoundError can happen if Hibernate 4.2 is used and some
            // containers (e.g. JBoss EAP 6) wrap it in the superclass LinkageError
            if (!isUsingJndi()) {
                throw new IllegalStateException("Unable to set Hibernate JTA platform, are you using the correct version of Hibernate?", ex);
            }
            // Assume that Hibernate will use JNDI
            if (log.isDebugEnabled()) {
                log.debug("Unable to set Hibernate JTA platform : " + ex.getMessage());
            }
        }
    }

    protected static List<HibernatePropertiesCustomizer> determineHibernatePropertiesCustomizers(
        PhysicalNamingStrategy physicalNamingStrategy, ImplicitNamingStrategy implicitNamingStrategy,
        ConfigurableListableBeanFactory beanFactory,
        List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        List<HibernatePropertiesCustomizer> customizers = new ArrayList<>();
        if (ClassUtils.isPresent("org.hibernate.resource.beans.container.spi.BeanContainer",
            HibernateCommon.class.getClassLoader())) {
            customizers.add((properties) -> properties.put(AvailableSettings.BEAN_CONTAINER,
                new SpringBeanContainer(beanFactory)));
        }
        if (physicalNamingStrategy != null || implicitNamingStrategy != null) {
            customizers
                .add(new NamingStrategiesHibernatePropertiesCustomizer(physicalNamingStrategy, implicitNamingStrategy));
        }
        customizers.addAll(hibernatePropertiesCustomizers);
        return customizers;
    }


    private static boolean isUsingJndi() {
        try {
            return JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable();
        } catch (Error ex) {
            return false;
        }
    }


    protected static boolean runningOnWebSphere() {
        return ClassUtils.isPresent("com.ibm.websphere.jtaextensions.ExtendedJTATransaction", HibernateCommon.class.getClassLoader());
    }

}
