package com.yiird.spring.boot.autoconfigure.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class EnvUtil {


    public static boolean isJta(Environment environment) {
        String property = environment.getProperty(PropertiesKeyConstants.JTA_ENABLED_PROPERTY);
        return ("true".equals(property) || !StringUtils.hasText(property)) && isMultiDataSource(environment);
    }

    public static boolean isSingleDataSource(Environment environment) {
        return !isMultiDataSource(environment);
    }

    public static boolean isMultiDataSource(Environment environment) {
        return Binder.get(environment)
            .bind(PropertiesKeyConstants.MULTI_DATASOURCE_PROPERTY, Map.class).isBound();
    }


    public static Set<String> packageChain(String[] basePackages, String... nestedPackages) {
        Set<String> packages = new HashSet<>();

        Set<String> ns = Arrays.stream(nestedPackages).map(it -> !it.startsWith(".") ? "." + it : it).collect(Collectors.toSet());

        for (int i = 0; i < basePackages.length; i++) {
            String basePackage = basePackages[i];

            if (basePackage.endsWith(".")) {
                packages.add(basePackage.substring(0, basePackage.length() - 1));
                ns.forEach(it -> {
                    packages.add(basePackage.substring(0, basePackage.length() - 1) + it);
                });
            } else {
                packages.add(basePackage);
                ns.forEach(it -> {
                    packages.add(basePackage + it);
                });
            }
        }

        return packages;
    }
}
