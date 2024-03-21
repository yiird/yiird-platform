package com.yiird.spring.boot.autoconfigure.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

public abstract class ConfigurableExcludeAutoConfiguration implements EnvironmentPostProcessor {

    private final static String PROPERTY_SOURCE_NAME = "yiird";

    protected Environment environment;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        this.environment = environment;
        String[] exitsExcludes = environment.getProperty("spring.autoconfigure.exclude",
            String[].class);

        Map<String, Object> kv = new HashMap<>();
        List<String> excludes = excludes();
        if (Objects.nonNull(exitsExcludes)) {
            excludes.addAll(Arrays.asList(exitsExcludes));
        }
        kv.put("spring.autoconfigure.exclude", excludes);
        MapPropertySource propertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, kv);
        environment.getPropertySources().addFirst(propertySource);
    }

    protected abstract List<String> excludes();
}
