dependencies {
    //spring boot bom
    optional("org.springframework.boot:spring-boot-starter-data-jpa")
    optional("org.springframework.boot:spring-boot-starter-json")

    //custom bom
    optional(platform(project(":yiird-dependencies")))
    optional("com.alibaba:druid")
    optional("com.atomikos:transactions-spring-boot3-starter")
    optional("ch.qos.logback:logback-classic")

    compileOnly("org.springframework.data:spring-data-envers")

}
