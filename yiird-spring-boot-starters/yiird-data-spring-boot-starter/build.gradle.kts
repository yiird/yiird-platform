dependencies {
    api(platform(project(":yiird-dependencies")))
    api(project(":yiird-spring-boot-autoconfigure"))
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("com.alibaba:druid")
    api("com.atomikos:transactions-spring-boot3-starter")
}