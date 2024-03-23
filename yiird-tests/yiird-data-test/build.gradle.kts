dependencies {
    implementation(project(":yiird-spring-boot-starters:yiird-data-spring-boot-starter"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.commons:commons-pool2")

    implementation("com.mysql:mysql-connector-j")
    implementation("org.hibernate.orm:hibernate-jcache")
}