dependencies {
    optional(platform(project(":yiird-dependencies")))

    optional("org.springframework.boot:spring-boot-autoconfigure")
    optional("org.springframework.boot:spring-boot-starter-data-jpa")
    optional("com.alibaba:druid")
    optional("com.atomikos:transactions-spring-boot3-starter")
    optional("ch.qos.logback:logback-classic")

    compileOnly("org.projectlombok:lombok")
    compileOnly("org.springframework.data:spring-data-envers")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}
