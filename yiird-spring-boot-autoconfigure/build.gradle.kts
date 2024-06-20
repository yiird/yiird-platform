dependencies {
    //spring boot bom
    optional("org.springframework.boot:spring-boot-starter-data-jpa")
    optional("org.springframework.boot:spring-boot-starter-json")
    optional("org.springframework.boot:spring-boot-starter-web")
    optional("jakarta.servlet:jakarta.servlet-api")



    //custom bom
    optional(platform(project(":yiird-dependencies")))
    optional("com.alibaba:druid")
    optional("com.atomikos:transactions-spring-boot3-starter")
    optional("ch.qos.logback:logback-classic")
    optional("org.apache.commons:commons-pool2")
    optional("commons-net:commons-net")
    optional("org.apache.tika:tika-core")
    /*optional("org.apache.tika:tika-parsers-standard-package")*/



    compileOnly("org.springframework.data:spring-data-envers")

}
