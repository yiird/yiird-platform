package com.yiird.data.test;

import java.util.Map;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

public class Test {

    public static void main(String[] args) {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(EnableJpaRepositories.class);
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableJpaRepositories.class.getName());
        System.out.println();
    }

}
