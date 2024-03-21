dependencies {
    implementation(project(":yiird-spring-boot-starters:yiird-data-spring-boot-starter"))

    implementation("com.mysql:mysql-connector-j")
    implementation("org.hibernate.orm:hibernate-jcache")

    optional("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}