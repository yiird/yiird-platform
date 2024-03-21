package com.yiird.spring.boot.autoconfigure.data.jpa.hibernate;

import java.util.Map;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

class NamingStrategiesHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {

    private final PhysicalNamingStrategy physicalNamingStrategy;

    private final ImplicitNamingStrategy implicitNamingStrategy;

    NamingStrategiesHibernatePropertiesCustomizer(
        PhysicalNamingStrategy physicalNamingStrategy,
        ImplicitNamingStrategy implicitNamingStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
        this.implicitNamingStrategy = implicitNamingStrategy;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        if (this.physicalNamingStrategy != null) {
            hibernateProperties.put("hibernate.physical_naming_strategy", this.physicalNamingStrategy);
        }
        if (this.implicitNamingStrategy != null) {
            hibernateProperties.put("hibernate.implicit_naming_strategy", this.implicitNamingStrategy);
        }
    }

}
